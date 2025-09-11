package com.digis01.DRosasAguilarDamianNCapasProject.RestController;

import com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva.CargaMasiva.CargaMasivaService;
import com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva.CargaMasiva.BulkJob;
import com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva.CargaMasiva.BulkStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.InputStream;
import java.util.Collection;
import java.util.Locale;

@RestController
@RequestMapping("/usuarioapi/cargamasiva")
public class UsuarioCargaMasivaController {

    private final CargaMasivaService service;

    public UsuarioCargaMasivaController(CargaMasivaService service) {
        this.service = service;
    }

    // ÚNICO endpoint de subida: acepta multipart/form-data O application/octet-stream
    @PostMapping(consumes = MediaType.ALL_VALUE)
    public ResponseEntity<BulkJob> upload(
            HttpServletRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(name = "sobrescribir", defaultValue = "false") boolean sobrescribir
    ) {
        try {
            byte[] data = null;
            String filename = null;

            // 1) Si vino como "file" (multipart)
            if (file != null && !file.isEmpty()) {
                data = file.getBytes();
                filename = (file.getOriginalFilename() != null && !file.getOriginalFilename().isBlank())
                        ? file.getOriginalFilename()
                        : "archivo.txt";
            }

            // 2) Si es multipart pero no usaron la key "file", toma el primer archivo disponible
            if (data == null || data.length == 0) {
                String ct = request.getContentType();
                boolean isMultipart = (ct != null && ct.toLowerCase(Locale.ROOT).startsWith("multipart/"));

                if (isMultipart) {
                    // via MultipartHttpServletRequest (si está disponible)
                    if (request instanceof MultipartHttpServletRequest mreq && !mreq.getFileMap().isEmpty()) {
                        MultipartFile any = mreq.getFileMap().values().iterator().next();
                        if (any != null && !any.isEmpty()) {
                            data = any.getBytes();
                            filename = (any.getOriginalFilename() != null && !any.getOriginalFilename().isBlank())
                                    ? any.getOriginalFilename()
                                    : "archivo.txt";
                        }
                    }
                    // via Servlet Parts (fallback)
                    if (data == null || data.length == 0) {
                        try {
                            Collection<Part> parts = request.getParts();
                            if (parts != null) {
                                for (Part p : parts) {
                                    String fn = p.getSubmittedFileName();
                                    if (fn != null && p.getSize() > 0) {
                                        data = p.getInputStream().readAllBytes();
                                        filename = fn;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }

            if (data == null || data.length == 0) {
                try (InputStream is = request.getInputStream()) {
                    data = is.readAllBytes();
                }
                if (data != null && data.length > 0) {
                    filename = request.getHeader("X-Filename");
                    if (filename == null || filename.isBlank()) filename = "archivo.txt"; // default con extensión
                }
            }

            if (data == null || data.length == 0) {
                BulkJob err = new BulkJob();
                err.status = BulkStatus.ERROR;
                err.observacion = "No se encontró archivo: usa form-data (key tipo File) o cuerpo binario con el archivo.";
                return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
            }

            BulkJob job = service.registrarUpload(filename, data, sobrescribir);
            HttpStatus st = (job.status == BulkStatus.ERROR) ? HttpStatus.CONFLICT : HttpStatus.OK;
            return new ResponseEntity<>(job, st);

        } catch (SecurityException se) {
            BulkJob err = new BulkJob();
            err.status = BulkStatus.ERROR;
            err.observacion = se.getMessage();
            return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            BulkJob err = new BulkJob();
            err.status = BulkStatus.ERROR;
            err.observacion = ex.getMessage();
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }

    // Procesar archivo previamente subido
    @PostMapping("/procesar/{id}")
    public ResponseEntity<BulkJob> procesar(@PathVariable String id) {
        try {
            BulkJob job = service.procesar(id);
            HttpStatus st = (job.status == BulkStatus.ERROR) ? HttpStatus.CONFLICT : HttpStatus.OK;
            return new ResponseEntity<>(job, st);
        } catch (Exception ex) {
            BulkJob err = new BulkJob();
            err.id = id;
            err.status = BulkStatus.ERROR;
            err.observacion = ex.getMessage();
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }
}

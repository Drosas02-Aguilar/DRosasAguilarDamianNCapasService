/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digis01.DRosasAguilarDamianNCapasProject.cargamasiva;

import com.digis01.DRosasAguilarDamianNCapasProject.DAO.IUsuarioJPADAO;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Result;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Usuario;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Direccion;
import com.digis01.DRosasAguilarDamianNCapasProject.JPA.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CargaMasiva {

    public enum BulkStatus { ERROR, PROCESAR, PROCESADO }

    public static class CargaError {
        public int fila;
        public String campo;
        public String mensaje;
        public CargaError() {}
        public CargaError(int fila, String campo, String mensaje) {
            this.fila = fila; this.campo = campo; this.mensaje = mensaje;
        }
    }

    public static class BulkJob {
        public String id;
        public String sha1;
        public String filename;
        public BulkStatus status;
        public Instant createdAt;
        public Instant deadline;
        public String observacion;
        public boolean sobrescribir; // controla reproceso/overwrite de archivo
        public int insertados;
        public int actualizados; // siempre 0 (no se actualiza en este modo)
        public int ignorados;
        public List<CargaError> errores = new ArrayList<>();
        @JsonIgnore public byte[] data;        // buffer del archivo (no se expone en JSON)
        public String contentType;
        @JsonIgnore public Path savedPath;     // ruta donde se guardó renombrado
    }

    // ===== DTOs =====
    public static class UsuarioDTO {
        public String nombre, apellidoPaterno, apellidoMaterno, sexo;
        public LocalDate fechaNacimiento;
        public String username, email, password, curp, celular, telefono;
        public Integer idRol;
        public String imagenBase64; // CLOB (base64 como texto)
    }
    public static class DireccionDTO {
        public String calle, numeroInterior, numeroExterior;
        public Integer idColonia;
    }
    public static class RegistroUsuarioDireccion {
        public int fila; // para reportar errores por fila
        public UsuarioDTO usuario;
        public DireccionDTO direccion;
        public RegistroUsuarioDireccion(int fila, UsuarioDTO u, DireccionDTO d) {
            this.fila = fila; this.usuario = u; this.direccion = d;
        }
    }

    // ===== Utils =====
    public static String sha1(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    public static LocalDate toLocalDate(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ===== Parser =====
    public static class CargaParser {
        public static class ParseResult {
            public final List<RegistroUsuarioDireccion> registros = new ArrayList<>();
            public final List<CargaError> errores = new ArrayList<>();
        }

        // Encabezados en el orden esperado (para archivos sin encabezado)
        private static final List<String> ORDERED_HEADERS = Arrays.asList(
                "NOMBRE","APELLIDOPATERNO","APELLIDOMATERNO","SEXO","FECHANACIMIENTO",
                "USERNAME","EMAIL","PASSWORD","CURP","CELULAR","TELEFONO","IDROL",
                "IMAGEN","CALLE","NUMEROINTERIOR","NUMEROEXTERIOR","IDCOLONIA"
        );
        private static final Set<String> HEADERS = new LinkedHashSet<>(ORDERED_HEADERS);

        public ParseResult parse(byte[] bytes, String filename) {
            String lower = filename.toLowerCase(Locale.ROOT);
            if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) return parseExcel(bytes);
            if (lower.endsWith(".txt") || lower.endsWith(".csv")) return parseTxt(bytes);
            ParseResult r = new ParseResult();
            r.errores.add(new CargaError(1, "ARCHIVO", "Formato no soportado: " + filename));
            return r;
        }

        private ParseResult parseExcel(byte[] bytes) {
            ParseResult out = new ParseResult();
            try (InputStream is = new ByteArrayInputStream(bytes);
                 Workbook wb = WorkbookFactory.create(is)) {
                DataFormatter fmt = new DataFormatter();
                Sheet sh = wb.getSheetAt(0);
                if (sh == null) {
                    out.errores.add(new CargaError(1, "ARCHIVO", "Hoja 0 vacía"));
                    return out;
                }

                Row header = sh.getRow(0);
                Map<String,Integer> idx = new LinkedHashMap<>();
                boolean tieneHeaderValido = false;

                if (header != null) {
                    for (int c = 0; c < header.getLastCellNum(); c++) {
                        String h = fmt.formatCellValue(header.getCell(c)).trim().toUpperCase(Locale.ROOT);
                        if (!h.isEmpty()) idx.put(h, c);
                    }
                    int encontrados = 0;
                    for (String h : HEADERS) if (idx.containsKey(h)) encontrados++;
                    // Heurística: si encontramos 60%+ de headers, consideramos que sí hay encabezado
                    tieneHeaderValido = (encontrados >= (int)Math.ceil(HEADERS.size() * 0.6));
                }

                int startRow = 1;
                if (!tieneHeaderValido) {
                    // sin encabezado: mapea por índice en el orden esperado
                    idx.clear();
                    for (int c = 0; c < ORDERED_HEADERS.size(); c++) {
                        idx.put(ORDERED_HEADERS.get(c), c);
                    }
                    startRow = 0; // primera fila ya es data
                }

                for (int r = startRow; r <= sh.getLastRowNum(); r++) {
                    Row row = sh.getRow(r);
                    if (row == null) continue;
                    RegistroUsuarioDireccion reg = rowToRegistro(row, idx, fmt, r + 1, out.errores);
                    if (reg != null) out.registros.add(reg);
                }
            } catch (Exception ex) {
                out.errores.add(new CargaError(1, "ARCHIVO", "No se pudo leer Excel: " + ex.getMessage()));
            }
            return out;
        }

        private RegistroUsuarioDireccion rowToRegistro(Row row,
                                                       Map<String,Integer> idx,
                                                       DataFormatter fmt,
                                                       int fila1,
                                                       List<CargaError> errores) {
            UsuarioDTO u = new UsuarioDTO();
            DireccionDTO d = new DireccionDTO();

            // helpers
            java.util.function.Function<String,String> getS = h -> {
                Integer c = idx.get(h);
                if (c == null) return null;
                String v = fmt.formatCellValue(row.getCell(c));
                return v != null ? v.trim() : null;
            };
            java.util.function.Function<String,LocalDate> getDate = h -> {
                Integer c = idx.get(h);
                if (c == null) return null;
                Cell cell = row.getCell(c);
                if (cell == null) return null;
                if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                    return toLocalDate(cell.getDateCellValue());
                }
                String v = fmt.formatCellValue(cell).trim();
                try { return LocalDate.parse(v); } catch (Exception ex) { return null; }
            };
            java.util.function.Function<String,Integer> getI = h -> {
                try {
                    String s = getS.apply(h);
                    return (s == null || s.isEmpty()) ? null : Integer.parseInt(s);
                } catch (Exception ex) { return null; }
            };

            u.nombre = getS.apply("NOMBRE");
            u.apellidoPaterno = getS.apply("APELLIDOPATERNO");
            u.apellidoMaterno = getS.apply("APELLIDOMATERNO");
            u.sexo = getS.apply("SEXO");
            u.fechaNacimiento = getDate.apply("FECHANACIMIENTO");
            u.username = getS.apply("USERNAME");
            u.email = getS.apply("EMAIL");
            u.password = getS.apply("PASSWORD");
            u.curp = getS.apply("CURP");
            u.celular = getS.apply("CELULAR");
            u.telefono = getS.apply("TELEFONO");
            u.idRol = getI.apply("IDROL");
            u.imagenBase64 = getS.apply("IMAGEN");

            d.calle = getS.apply("CALLE");
            d.numeroInterior = getS.apply("NUMEROINTERIOR");
            d.numeroExterior = getS.apply("NUMEROEXTERIOR");
            d.idColonia = getI.apply("IDCOLONIA");

            validar(u, d, fila1, errores);
            if (errores.stream().anyMatch(e -> e.fila == fila1)) return null;

            return new RegistroUsuarioDireccion(fila1, u, d);
        }

        private ParseResult parseTxt(byte[] bytes) {
            ParseResult out = new ParseResult();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8))) {

                String first = br.readLine();
                if (first == null) {
                    out.errores.add(new CargaError(1, "ARCHIVO", "Archivo vacío"));
                    return out;
                }

                String[] firstCols = first.split("[|,]", -1);
                Map<String,Integer> idx = new LinkedHashMap<>();
                boolean tieneHeaderValido = false;

                // ¿La primera línea parece encabezado?
                for (int i = 0; i < firstCols.length; i++) {
                    String h = firstCols[i].trim().toUpperCase(Locale.ROOT);
                    if (!h.isEmpty()) idx.put(h, i);
                }
                int encontrados = 0;
                for (String need : HEADERS) if (idx.containsKey(need)) encontrados++;
                tieneHeaderValido = (encontrados >= (int)Math.ceil(HEADERS.size() * 0.6));

                if (!tieneHeaderValido) {
                    // sin encabezado: mapea por índice (orden fijo)
                    idx.clear();
                    for (int i = 0; i < ORDERED_HEADERS.size(); i++) {
                        idx.put(ORDERED_HEADERS.get(i), i);
                    }
                    // procesa la primera línea como data (fila = 1)
                    RegistroUsuarioDireccion reg = parseTxtLineAsRegistro(firstCols, idx, 1, out.errores);
                    if (reg != null) out.registros.add(reg);
                }

                // resto de líneas
                String line; int fila = 1;
                while ((line = br.readLine()) != null) {
                    fila++;
                    if (line.trim().isEmpty()) continue;
                    String[] c = line.split("[|,]", -1);

                    RegistroUsuarioDireccion reg = parseTxtLineAsRegistro(c, idx, fila, out.errores);
                    if (reg != null) out.registros.add(reg);
                }
            } catch (Exception ex) {
                out.errores.add(new CargaError(1, "ARCHIVO", "No se pudo leer TXT/CSV: " + ex.getMessage()));
            }
            return out;
        }

        private RegistroUsuarioDireccion parseTxtLineAsRegistro(
                String[] c, Map<String,Integer> idx, int fila, List<CargaError> errores) {

            java.util.function.Function<String,String> getS = key -> {
                Integer i = idx.get(key);
                if (i == null || i >= c.length) return null;
                String v = c[i];
                return v != null ? v.trim() : null;
            };
            java.util.function.Function<String,Integer> getI = key -> {
                try {
                    String s = getS.apply(key);
                    return (s == null || s.isEmpty()) ? null : Integer.parseInt(s);
                } catch (Exception ex) { return null; }
            };
            java.util.function.Function<String,LocalDate> getD = key -> {
                String s = getS.apply(key);
                if (s == null || s.isEmpty()) return null;
                try { return LocalDate.parse(s); } catch (Exception ex) { return null; }
            };

            UsuarioDTO u = new UsuarioDTO();
            DireccionDTO d = new DireccionDTO();
            u.nombre = getS.apply("NOMBRE");
            u.apellidoPaterno = getS.apply("APELLIDOPATERNO");
            u.apellidoMaterno = getS.apply("APELLIDOMATERNO");
            u.sexo = getS.apply("SEXO");
            u.fechaNacimiento = getD.apply("FECHANACIMIENTO");
            u.username = getS.apply("USERNAME");
            u.email = getS.apply("EMAIL");
            u.password = getS.apply("PASSWORD");
            u.curp = getS.apply("CURP");
            u.celular = getS.apply("CELULAR");
            u.telefono = getS.apply("TELEFONO");
            u.idRol = getI.apply("IDROL");
            u.imagenBase64 = getS.apply("IMAGEN");

            d.calle = getS.apply("CALLE");
            d.numeroInterior = getS.apply("NUMEROINTERIOR");
            d.numeroExterior = getS.apply("NUMEROEXTERIOR");
            d.idColonia = getI.apply("IDCOLONIA");

            validar(u, d, fila, errores);

            boolean tieneErroresFila = errores.stream().anyMatch(e -> e.fila == fila);
            return tieneErroresFila ? null : new RegistroUsuarioDireccion(fila, u, d);
        }

        private void validar(UsuarioDTO u, DireccionDTO d, int fila1, List<CargaError> errores) {
            if (u.nombre == null || u.nombre.isEmpty()) errores.add(new CargaError(fila1,"NOMBRE","Requerido"));
            if (u.apellidoPaterno == null || u.apellidoPaterno.isEmpty()) errores.add(new CargaError(fila1,"APELLIDOPATERNO","Requerido"));
            if (u.sexo == null || u.sexo.isEmpty() || !(u.sexo.equalsIgnoreCase("H") || u.sexo.equalsIgnoreCase("M")))
                errores.add(new CargaError(fila1,"SEXO","Debe ser H o M"));
            if (u.fechaNacimiento == null) errores.add(new CargaError(fila1,"FECHANACIMIENTO","Formato válido (yyyy-MM-dd o fecha de Excel)"));
            if (u.username == null || u.username.isEmpty()) errores.add(new CargaError(fila1,"USERNAME","Requerido"));
            if (u.email == null || u.email.isEmpty() || !u.email.contains("@"))
                errores.add(new CargaError(fila1,"EMAIL","Email inválido"));
            if (u.password == null || u.password.isEmpty()) errores.add(new CargaError(fila1,"PASSWORD","Requerido"));
            if (u.curp == null || u.curp.isEmpty()) errores.add(new CargaError(fila1,"CURP","Requerido"));
            if (u.idRol == null) errores.add(new CargaError(fila1,"IDROL","Requerido"));
            if (d.idColonia == null) errores.add(new CargaError(fila1,"IDCOLONIA","Requerido"));
        }
    }

    // ===== Servicio (POJO) =====
    public static class CargaMasivaService {
        private final IUsuarioJPADAO usuarioDAO;
        private final EntityManager em;
        private final CargaParser parser = new CargaParser();
        private final long ttlMinutes;
        private final Path baseDir;

        // Log
        private final Path logFile;
        private static final DateTimeFormatter LOG_TS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        private final Map<String,BulkJob> jobs = new ConcurrentHashMap<>();
        private final Map<String,String> sha1ToJobId = new ConcurrentHashMap<>();

        // Excepción controlada para duplicados
        private static class DuplicateUserException extends RuntimeException {
            public DuplicateUserException(String msg) { super(msg); }
        }

        public CargaMasivaService(IUsuarioJPADAO usuarioDAO,
                                  EntityManager em,
                                  long ttlMinutes,
                                  String baseDir,
                                  String logFilePath) {
            this.usuarioDAO = usuarioDAO;
            this.em = em;
            this.ttlMinutes = ttlMinutes;
            this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
            this.logFile = Paths.get(logFilePath).toAbsolutePath().normalize();
            try {
                Path parent = this.logFile.getParent();
                if (parent != null) Files.createDirectories(parent);
                Files.createDirectories(this.baseDir);
            } catch (Exception ignore) {}
        }

        /** Checa en el LOG si existe un PROCESADO para el SHA1 (persistente entre reinicios). */
        private boolean isShaProcessedInLog(String sha1) {
            try {
                if (!Files.exists(logFile)) return false;
                // Lee todo el archivo de log (suficiente para volúmenes normales)
                List<String> lines = Files.readAllLines(logFile, StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (!line.startsWith("log ")) continue;
                    // Formato: log <sha1>|<Nombre>|<STATUS>|<ts>|<mensaje>
                    int firstSpace = line.indexOf(' ');
                    if (firstSpace < 0) continue;
                    String rest = line.substring(firstSpace + 1);
                    String[] parts = rest.split("\\|", -1);
                    if (parts.length < 3) continue;
                    String sha = parts[0];
                    String status = parts[2];
                    if (sha.equalsIgnoreCase(sha1) && "PROCESADO".equalsIgnoreCase(status)) {
                        return true;
                    }
                }
            } catch (Exception ignored) {}
            return false;
        }

        /** POST /usuarioapi/cargamasiva (Multipart o Binario)
         *  - Calcula SHA1
         *  - Si LOG ya tiene PROCESADO para ese SHA1 y sobrescribir=false -> ERROR
         *  - Guarda archivo como {sha1}_{nombreOriginal}:
         *      - sobrescribir=false: no sobreescribe si ya existe
         *      - sobrescribir=true : sobrescribe
         *  - Registra job en PROCESAR y LOG "Registrado"
         */
        public BulkJob registrarUpload(String originalName, byte[] data, boolean sobrescribir) {
            try {
                String sha1 = CargaMasiva.sha1(data);

                // Validación por LOG persistente
                if (isShaProcessedInLog(sha1) && !sobrescribir) {
                    BulkJob j = new BulkJob();
                    j.id = UUID.randomUUID().toString();
                    j.filename = originalName == null ? "archivo" : originalName.replaceAll("[\\r\\n]", "");
                    j.sha1 = sha1;
                    j.status = BulkStatus.ERROR;
                    j.observacion = "Mismo archivo ya PROCESADO (por LOG)";
                    writeLog(j.sha1, j.filename, j.status, "Mismo archivo ya PROCESADO (por LOG)", Instant.now());
                    return j;
                }

                // Guardar renombrado
                String safeName = (originalName == null ? "archivo" : originalName.replaceAll("[\\r\\n]", ""));
                String newName = sha1 + "_" + safeName;
                Path target = baseDir.resolve(newName).normalize();
                if (!target.startsWith(baseDir)) throw new SecurityException("Ruta fuera de base-dir");

                if (Files.exists(target)) {
                    if (sobrescribir) {
                        Files.write(target, data, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                    } else {
                        // no sobreescribimos; conservamos el existente
                    }
                } else {
                    Files.write(target, data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                }

                BulkJob job = new BulkJob();
                job.id = UUID.randomUUID().toString();
                job.filename = safeName;
                job.sha1 = sha1;
                job.status = BulkStatus.PROCESAR;
                job.createdAt = Instant.now();
                job.deadline = job.createdAt.plusSeconds(ttlMinutes * 60);
                job.observacion = "Cargado. En espera de procesar.";
                job.data = data;
                job.contentType = guessContentTypeByName(safeName);
                job.sobrescribir = sobrescribir;
                job.savedPath = target;

                jobs.put(job.id, job);
                sha1ToJobId.put(sha1, job.id);

                writeLog(job.sha1, job.filename, BulkStatus.PROCESAR, "Registrado", job.createdAt);
                return job;
            } catch (Exception ex) {
                BulkJob err = new BulkJob();
                err.id = null;
                err.status = BulkStatus.ERROR;
                err.observacion = ex.getMessage();
                writeLog("unknown", originalName == null ? "archivo" : originalName, BulkStatus.ERROR, ex.getMessage(), Instant.now());
                return err;
            }
        }

        /** POST /usuarioapi/cargamasiva/procesar/{id} */
        public BulkJob procesar(String id) {
            BulkJob job = jobs.get(id);
            if (job == null) {
                BulkJob err = new BulkJob();
                err.id = id;
                err.status = BulkStatus.ERROR;
                err.observacion = "uploadId no encontrado";
                writeLog("unknown", "unknown", err.status, err.observacion, Instant.now());
                return err;
            }

            // TTL vencido
            if (Instant.now().isAfter(job.deadline)) {
                job.status = BulkStatus.ERROR;
                job.observacion = "Se pasó el límite de tiempo para procesar (" + ttlMinutes + " min).";
                writeLog(job.sha1, job.filename, job.status, "Tiempo expirado", Instant.now());
                return job;
            }

            // Si ya finalizó, solo loguea el estado y devuelve
            if (job.status == BulkStatus.PROCESADO || job.status == BulkStatus.ERROR) {
                writeLog(job.sha1, job.filename, job.status, job.observacion, Instant.now());
                return job;
            }

            // Parseo / validación
            CargaParser.ParseResult pr = parser.parse(job.data, job.filename);
            job.errores.addAll(pr.errores);
            if (!job.errores.isEmpty()) {
                job.status = BulkStatus.ERROR;
                job.observacion = "Errores en formato/validación. No se procesa";
                writeLog(job.sha1, job.filename, job.status, "No se procesa", Instant.now());
                return job;
            }

            // Procesamiento (solo INSERT; duplicados -> error por fila)
            for (RegistroUsuarioDireccion r : pr.registros) {
                try {
                    int res = insertUsuarioDireccion(r.usuario, r.direccion);
                    if (res == 1) job.insertados++;
                    else job.ignorados++;
                } catch (DuplicateUserException dup) {
                    job.errores.add(new CargaError(r.fila, "USERNAME/CURP", dup.getMessage()));
                    job.ignorados++;
                } catch (Exception ex) {
                    job.errores.add(new CargaError(r.fila, "SP/DB", ex.getMessage()));
                }
            }

            if (!job.errores.isEmpty() && job.insertados == 0) {
                job.status = BulkStatus.ERROR;
                job.observacion = "Falló total/parcial. No se insertó.";
                writeLog(job.sha1, job.filename, job.status, "No se procesa", Instant.now());
            } else {
                job.status = BulkStatus.PROCESADO;
                job.observacion = "Procesado archivo en DB.";
                writeLog(job.sha1, job.filename, job.status, "Ok", Instant.now());
            }
            return job;
        }

        public Optional<BulkJob> getJob(String id) { return Optional.ofNullable(jobs.get(id)); }

        // ===== INSERT ONLY =====
        private int insertUsuarioDireccion(UsuarioDTO u, DireccionDTO d) {
            Usuario existente = findByUsernameOrCurp(u.username, u.curp);
            if (existente != null) {
                throw new DuplicateUserException("Usuario ya existe (USERNAME/CURP)");
            }
            Usuario nuevo = new Usuario();
            mapUsuario(nuevo, u);
            mapOrAttachDireccion(nuevo, d);
            Result r = usuarioDAO.Add(nuevo);
            if (r.correct) return 1;
            throw new RuntimeException(r.errorMessage != null ? r.errorMessage : "Fallo Add()");
        }

        private Usuario findByUsernameOrCurp(String username, String curp) {
            TypedQuery<Usuario> q = em.createQuery(
                    "FROM Usuario u WHERE u.Username = :u OR u.Curp = :c ORDER BY u.IdUsuario",
                    Usuario.class);
            q.setParameter("u", username);
            q.setParameter("c", curp);
            List<Usuario> list = q.getResultList();
            return list.isEmpty() ? null : list.get(0);
        }

        // ====== CAMBIO: Rol como referencia administrada; sin tocar tus entidades
        private void mapUsuario(Usuario target, UsuarioDTO u) {
            target.setNombre(u.nombre);
            target.setApellidopaterno(u.apellidoPaterno);
            target.setApellidomaterno(u.apellidoMaterno);
            target.setSexo(u.sexo);
            target.setFechaNacimiento(u.fechaNacimiento != null ? java.sql.Date.valueOf(u.fechaNacimiento) : null);
            target.setUsername(u.username);
            target.setEmail(u.email);
            target.setPassword(u.password);
            target.setCurp(u.curp);
            target.setCelular(u.celular);
            target.setTelefono(u.telefono);
            target.setImagen(u.imagenBase64); // CLOB (String Base64)

            if (u.idRol != null) {
                try {
                    Rol refRol = em.getReference(Rol.class, u.idRol);
                    if (target.Rol == null) {
                        target.Rol = refRol; // tu campo público 'Rol'
                    } else {
                        target.Rol.setIdRol(refRol.getIdRol()); // asegura el id
                    }
                } catch (Exception ignored) {
                    // Si no existe el rol, la BD (FK NOT NULL) lo hará fallar — comportamiento esperado
                }
            }
        }

        // ====== CAMBIO: amarrar direccion al usuario y colonia como referencia
        private void mapOrAttachDireccion(Usuario usuario, DireccionDTO d) {
            if (d == null) return;

            List<Direccion> lista = usuario.getDirecciones();
            if (lista == null) {
                lista = new ArrayList<>();
                usuario.setDirecciones(lista);
            }

            Direccion dir = new Direccion();

            // Setters típicos (por reflexión para no tocar tus entidades)
            try { Direccion.class.getMethod("setCalle", String.class).invoke(dir, d.calle); } catch (Exception ignored) {}
            try { Direccion.class.getMethod("setNumeroInterior", String.class).invoke(dir, d.numeroInterior); } catch (Exception ignored) {}
            try { Direccion.class.getMethod("setNumeroExterior", String.class).invoke(dir, d.numeroExterior); } catch (Exception ignored) {}

            // setUsuario si existe (lado dueño)
            try { Direccion.class.getMethod("setUsuario", Usuario.class).invoke(dir, usuario); } catch (Exception ignored) {}

            // idColonia: referencia administrada si hay entidad Colonia; si no, setIdColonia
            if (d.idColonia != null) {
                boolean asignado = false;
                try {
                    Class<?> coloniaCls = Class.forName("com.digis01.DRosasAguilarDamianNCapasProject.JPA.Colonia");
                    Object refCol = em.getReference(coloniaCls, d.idColonia);
                    Direccion.class.getMethod("setColonia", coloniaCls).invoke(dir, refCol);
                    asignado = true;
                } catch (Exception ignored) {}
                if (!asignado) {
                    try { Direccion.class.getMethod("setIdColonia", Integer.class).invoke(dir, d.idColonia); } catch (Exception ignored) {}
                }
            }

            lista.add(dir);
        }

        // ===== LOG =====
        private static String statusWord(BulkStatus s) {
            return switch (s) {
                case ERROR -> "ERROR";
                case PROCESAR -> "PROCESAR";
                case PROCESADO -> "PROCESADO";
            };
        }
        private void writeLog(String sha1, String filename, BulkStatus status, String message, Instant ts) {
            String line = "log " + sha1 + "|" + filename + "|" +
                    statusWord(status) + "|" +
                    LOG_TS.format(ts.atZone(ZoneId.systemDefault())) + "|" +
                    (message == null ? "" : message) +
                    System.lineSeparator();
            try {
                Files.writeString(
                        logFile, line, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND
                );
            } catch (Exception e) {
                // no interrumpir el flujo por error de log
            }
        }

        private static String guessContentTypeByName(String name) {
            String lower = name.toLowerCase(Locale.ROOT);
            if (lower.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            if (lower.endsWith(".xls"))  return "application/vnd.ms-excel";
            if (lower.endsWith(".csv"))  return "text/csv";
            if (lower.endsWith(".txt"))  return "text/plain";
            return "application/octet-stream";
        }
    }

    // ===== Configuración HARDCODE (sin application.properties) =====
    @Configuration
    public static class BeansConfig {
        @Bean
        public CargaMasivaService cargaMasivaService(
                IUsuarioJPADAO usuarioDAO,
                EntityManager em
        ) {
            long ttl = 1L;                         
            String baseDir = "archivos";           
            String logFile = "archivos/cargamasiva.log";
            return new CargaMasivaService(usuarioDAO, em, ttl, baseDir, logFile);
        }

        // Resolver para multipart (asegura que Spring trate la request como multipart)
        @Bean
        public StandardServletMultipartResolver multipartResolver() {
            return new StandardServletMultipartResolver();
        }
    }
}

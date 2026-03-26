package lazy.dev.LataFile;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class LataFile {
    public static final String _LATA = "1.2-snapshot";
    private static final Logger LOGGER = Logger.getLogger("LataFormat");

    private final Map<String, Map<String, Object>> data = new LinkedHashMap<>();
    private List<String> readOnlySections = new ArrayList<>();

    public LataFile() {}
    public void load(File file) throws IOException {
        data.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line, section = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line.substring(1, line.length() - 1);
                    data.putIfAbsent(section, new LinkedHashMap<>());
                } else if (line.contains("=") && section != null) {
                    String[] p = line.split("=", 2);
                    data.get(section).put(p[0].trim(), parse(p[1].trim()));
                }
            }
        }
        if (!data.containsKey("meta")) {
            throw new IOException("Cannot found [meta] section inside .lata - " + file.getName());
        }

        updatePermissions();
        checkAndFixVersion(file);
    }
    private void checkAndFixVersion(File file) throws IOException {
        Map<String, Object> meta = data.computeIfAbsent("meta", k -> new HashMap<>());
        String fileVersion = String.valueOf(meta.getOrDefault("version", "unknown"));

        if (!_LATA.equals(fileVersion)) {
            LOGGER.warning("[LATA] Version mismatch! File: " + fileVersion + " | Target: " + _LATA);
            throw new IOException("Unsupported Lata version");
        }
    }
    private void updatePermissions() {
        if (data.containsKey("meta") && data.get("meta").containsKey("readonly")) {
            String raw = data.get("meta").get("readonly").toString();
            readOnlySections = Arrays.asList(raw.replaceAll("[\\[\\]\"]", "").split(","));
        }
    }

    public void setValue(String section, String key, Object value) {
        if (readOnlySections.contains(section)) {
            throw new SecurityException("Cannot modify read-only section: " + section);
        }
        data.computeIfAbsent(section, k -> new HashMap<>()).put(key, value);
    }
    public Object get(String section, String key) {
        // After you get it, convert to type that you need.
        Map<String, Object> sectionData = data.get(section);
        return (sectionData != null) ? sectionData.get(key) : null;
    }

    private Object parse(String v) {
        if (v.startsWith("\"")) return v.substring(1, v.length() - 1);
        if (v.equals("true") || v.equals("false")) return Boolean.parseBoolean(v);
        if (v.endsWith("f")) return Float.parseFloat(v.replace("f", ""));
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return v; }
    }
    public void saveToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (data.containsKey("meta")) {
                writeSection(writer, "meta", data.get("meta"));
            } else {
                throw new IOException("Cannot save: Missing [meta] section!");
            }

            for (Map.Entry<String, Map<String, Object>> sectionEntry : data.entrySet()) {
                if (sectionEntry.getKey().equals("meta")) continue;
                writeSection(writer, sectionEntry.getKey(), sectionEntry.getValue());
            }
        }
    }

    private void writeSection(BufferedWriter writer, String name, Map<String, Object> content) throws IOException {
        writer.write("[" + name + "]");
        writer.newLine();
        for (Map.Entry<String, Object> entry : content.entrySet()) {
            Object val = entry.getValue();
            String formatted = (val instanceof Float) ? val + "f" :
                    (val instanceof String) ? "\"" + val + "\"" : val.toString();
            writer.write(entry.getKey() + " = " + formatted);
            writer.newLine();
        }
        writer.newLine();
    }

    private static String getString(Map.Entry<String, Object> entry) {
        Object value = entry.getValue();

        return switch (value) {
            case String s -> "\"" + value + "\"";
            case Float v -> value + "f";
            case Boolean b -> value.toString().toLowerCase();
            default -> value.toString();
        };
    }

    public Map<String, Map<String, Object>> getData() {
        return data;
    }
}
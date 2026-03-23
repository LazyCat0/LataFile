package lazy.dev.lata.Script;

import lazy.dev.lata.File.LataFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class LataScript {
    private final LataFile script;
    private final LataFile dataContext;
    private final Map<String, BiConsumer<String, Map<String, Object>>> commands = new HashMap<>();

    public LataScript(File scriptFile, LataFile dataContext) throws IOException {
        this.script = new LataFile();
        this.script.load(scriptFile);
        this.dataContext = dataContext;

        setupDefaultCommands();
        checkAndRun();
    }

    private void setupDefaultCommands() {
        registerCommand("print", (args, sec) -> System.out.println("[Lata] " + args.replace("\"", "")));

        registerCommand("if", (condition, sec) -> {
            if (evaluate(condition.trim())) {
                executeBlock(String.valueOf(sec.get("then")), sec);
            } else if (sec.containsKey("else")) {
                executeBlock(String.valueOf(sec.get("else")), sec);
            }
        });
        registerCommand("set", (args, sec) -> {
            try {
                String[] parts = args.split(" ");
                String[] path = parts[0].split("\\.");
                String op = parts[2];
                int val1 = Integer.parseInt(dataContext.getData().get(path[0]).get(path[1]).toString());
                int val2 = Integer.parseInt(parts[3]);

                int result = op.equals("+") ? val1 + val2 : val1 - val2;
                dataContext.setValue(path[0], path[1], result);
            } catch (Exception e) {
                System.err.println("Math error: " + e.getMessage());
            }
        });
    }

    public void registerCommand(String name, BiConsumer<String, Map<String, Object>> action) {
        commands.put(name, action);
    }
    private void executeBlock(String content, Map<String, Object> sec) {
        String raw = content.trim();
        if (raw.startsWith("{") && raw.endsWith("}")) {
            String body = raw.substring(1, raw.length() - 1);
            for (String line : body.split(";")) {
                parseAndRunLine(line.trim(), sec);
            }
        } else {
            parseAndRunLine(raw, sec);
        }
    }

    private void parseAndRunLine(String line, Map<String, Object> sec) {
        if (line.isEmpty()) return;
        String[] parts = line.split(" ", 2);
        String cmd = parts[0].trim();
        String args = parts.length > 1 ? parts[1].trim() : "";

        if (commands.containsKey(cmd)) {
            commands.get(cmd).accept(args, sec);
        }
    }

    public void runSection(String sectionName) {
        Map<String, Object> section = script.getData().get(sectionName);
        if (section == null) return;

        for (String key : section.keySet()) {
            if (commands.containsKey(key)) {
                executeBlock(String.valueOf(section.get(key)), section);
            }
        }
    }

    private boolean evaluate(String cond) {
        try {
            String[] p = cond.split(" ");
            String[] path = p[0].split("\\.");
            Object raw = dataContext.getData().get(path[0]).get(path[1]);

            int current = Integer.parseInt(raw.toString());
            int target = Integer.parseInt(p[2]);
            String op = p[1];

            return switch (op) {
                case ">" -> current > target;
                case "<" -> current < target;
                case "==" -> current == target;
                default -> false;
            };
        } catch (Exception e) { return false; }
    }

    private void checkAndRun() {
        Map<String, Object> meta = script.getData().get("meta");
        if (meta != null && "true".equals(String.valueOf(meta.get("autorun")))) {
            runSection(String.valueOf(meta.getOrDefault("entry_point", "main")));
        }
    }
}

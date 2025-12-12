package Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class JsonReader {

    private final Object root;

    public JsonReader(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            this.root = parse(new JsonTokenizer(jsonText));
        }
    }

    public Map<String, Object> getRootObject() {
        if (root instanceof Map) {
            return (Map<String, Object>) root;
        }
        throw new IllegalStateException("Root of JSON is not an object.");
    }

    // Get a sub-object safely
    public Map<String, Object> getObject(String key) {
        Map<String, Object> rootObj = getRootObject();
        Object obj = rootObj.get(key);
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return null;
    }

    public int getObjectSize(String key) {
        Map<String, Object> obj = getObject(key);
        return obj == null ? 0 : obj.size();
    }

    // --- JSON parsing logic ---
    private static Object parse(JsonTokenizer t) {
        t.skipWhitespace();
        char c = t.peek();
        if (c == '{') return parseObject(t);
        if (c == '[') return parseArray(t);
        if (c == '"') return parseString(t);
        if (Character.isDigit(c) || c == '-') return parseNumber(t);
        if (t.startsWith("true")) { t.advance(4); return Boolean.TRUE; }
        if (t.startsWith("false")) { t.advance(5); return Boolean.FALSE; }
        if (t.startsWith("null")) { t.advance(4); return null; }
        throw new RuntimeException("Unexpected token at position " + t.pos);
    }

    private static Map<String, Object> parseObject(JsonTokenizer t) {
        Map<String, Object> map = new LinkedHashMap<>();
        t.expect('{');
        t.skipWhitespace();
        if (t.peek() == '}') { t.advance(); return map; }

        while (true) {
            t.skipWhitespace();
            String key = parseString(t);
            t.skipWhitespace();
            t.expect(':');
            t.skipWhitespace();
            Object value = parse(t);
            map.put(key, value);
            t.skipWhitespace();
            char next = t.peek();
            if (next == '}') { t.advance(); break; }
            t.expect(',');
        }
        return map;
    }

    private static List<Object> parseArray(JsonTokenizer t) {
        List<Object> list = new ArrayList<>();
        t.expect('[');
        t.skipWhitespace();
        if (t.peek() == ']') { t.advance(); return list; }

        while (true) {
            t.skipWhitespace();
            list.add(parse(t));
            t.skipWhitespace();
            char next = t.peek();
            if (next == ']') { t.advance(); break; }
            t.expect(',');
        }
        return list;
    }

    private static String parseString(JsonTokenizer t) {
        t.expect('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (t.isAtEnd()) throw new RuntimeException("Unterminated string");
            char c = t.advance();
            if (c == '"') break;
            if (c == '\\') { // handle escapes
                char next = t.advance();
                switch (next) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        String hex = "" + t.advance() + t.advance() + t.advance() + t.advance();
                        sb.append((char) Integer.parseInt(hex, 16));
                        break;
                    default: throw new RuntimeException("Invalid escape sequence: \\" + next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static Number parseNumber(JsonTokenizer t) {
        StringBuilder sb = new StringBuilder();
        if (t.peek() == '-') sb.append(t.advance());
        while (!t.isAtEnd() && (Character.isDigit(t.peek()) || t.peek() == '.')) {
            sb.append(t.advance());
        }
        String num = sb.toString();
        if (num.contains(".")) return Double.parseDouble(num);
        else return Long.parseLong(num);
    }

    // --- inner tokenizer class ---
    private static class JsonTokenizer {
        private final String text;
        private int pos = 0;

        JsonTokenizer(String text) { this.text = text.trim(); }

        boolean isAtEnd() { return pos >= text.length(); }
        char peek() { return text.charAt(pos); }
        char advance() { return text.charAt(pos++); }

        void skipWhitespace() {
            while (!isAtEnd() && Character.isWhitespace(peek())) pos++;
        }

        boolean startsWith(String s) {
            return text.startsWith(s, pos);
        }

        void advance(int n) { pos += n; }

        void expect(char c) {
            if (isAtEnd() || peek() != c)
                throw new RuntimeException("Expected '" + c + "' at position " + pos);
            pos++;
        }
    }
}

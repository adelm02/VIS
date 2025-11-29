package service;

import data.LekceDto;
import data.LekceTableModule;
import domain.Lekce;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LekceService {
    private static final String CSV_FILE = "lekce.csv";

    public void saveAllToFile(LekceTableModule table) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (Lekce l : table.getAll()) {
                // Formát: id,title,trainer,day,time,capacity,loggedIn,price
                String line = String.format("%d,%s,%s,%s,%s,%d,%d,%d",
                        l.id,
                        escapeCsv(l.title),
                        escapeCsv(l.trainer),
                        escapeCsv(l.day),
                        escapeCsv(l.time),
                        l.capacity,
                        l.loggedIn,
                        l.price.getAmountCzk()
                );
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("Uloženo " + table.getAll().size() + " lekcí do " + CSV_FILE);
    }

    public List<LekceDto> loadAllFromFile() throws Exception {
        List<LekceDto> lessons = new ArrayList<>();

        File file = new File(CSV_FILE);
        if (!file.exists()) {
            System.out.println("Soubor " + CSV_FILE + " neexistuje, vracím prázdný seznam");
            return lessons;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue; // Přeskočit prázdné řádky
                }

                try {
                    LekceDto dto = parseCsvLine(line);
                    lessons.add(dto);
                } catch (Exception e) {
                    System.err.println("Chyba na řádku " + lineNumber + ": " + e.getMessage());
                    System.err.println("Řádek: " + line);
                }
            }
        }

        System.out.println("Načteno " + lessons.size() + " lekcí z " + CSV_FILE);
        return lessons;
    }


    private LekceDto parseCsvLine(String line) {
        String[] parts = line.split(",");

        if (parts.length != 8) {
            throw new IllegalArgumentException(
                    "Neplatný formát CSV - očekáváno 8 polí, nalezeno " + parts.length
            );
        }

        LekceDto dto = new LekceDto();
        dto.id = Integer.parseInt(parts[0].trim());
        dto.title = parts[1].trim();
        dto.trainer = parts[2].trim();
        dto.day = parts[3].trim();
        dto.time = parts[4].trim();
        dto.capacity = Integer.parseInt(parts[5].trim());
        dto.loggedIn = Integer.parseInt(parts[6].trim());
        dto.price = Integer.parseInt(parts[7].trim());

        return dto;
    }


    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        // Pokud obsahuje čárku nebo uvozovky, zabal do uvozovek
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
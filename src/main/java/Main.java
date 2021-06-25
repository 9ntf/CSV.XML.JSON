import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileCSV = "data.csv";
        String fileXML = "data.xml";
        String dataJSON1 = "data.json";
        String dataJSON2 = "data2.json";

        //CSV to JSON
        try (CSVReader csvReader = new CSVReader(new FileReader(fileCSV))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            List<Employee> list = csvToBean.parse();
            String json = listToJson(list);
            writeString(json, dataJSON1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        //XML to JSON
        List<Employee> list = parseXML(fileXML);
        String json = listToJson(list);
        writeString(json, dataJSON2);

        //JSON to Object
        String readJson = readString(dataJSON1);
        List<Employee> employees = jsonToList(readJson);
        for (Employee emp : employees) {
            System.out.println(emp.toString());
        }

    }

    private static String listToJson(List list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type lisType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, lisType);
        return json;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            NodeList nodeList = doc.getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    employees.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return employees;
    }

    private static String readString(String fileName) {
        String readString = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JsonElement element = gson.fromJson(reader, JsonElement.class);
            readString = gson.toJson(element);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return readString;
    }

    private static List<Employee> jsonToList(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type type = new TypeToken<ArrayList<Employee>>() {
        }.getType();
        List<Employee> temp = gson.fromJson(json, type);
        return temp;
    }
}
import java.util.*;

public class Prog1 {

    static void p1() {
        String myString = "automatidfsfwdwdsxasdsdasdon";
        Map<Character, Integer> myMap = new LinkedHashMap<>();
        for (int i = 0; i < myString.length(); i++) {
            int count = 0;
            Character c = myString.charAt(i);
            if (myMap.get(c) == null) {
                count = 0;
            } else count = myMap.get(c);
            myMap.put(c, count + 1);

        }
        System.out.println(myMap);
    }

    static void p2() {
        String myString = "automatidfsfwdwdsxasdsdasdon";
        Map<Character, Integer> myMap = new LinkedHashMap<>();
        for (int i = 0; i < myString.length(); i++) {
            int count = 0;
            Character c = myString.charAt(i);
            if (myMap.get(c) == null) {
                count = 0;
            } else count = myMap.get(c);
            myMap.put(c, count + 1);

        }
        System.out.println(myMap);
        for (Map.Entry<Character, Integer> entry : myMap.entrySet()) {
            if (entry.getValue() == 1) {
                System.out.println(entry.getKey());
                return;
            }
        }
    }

    static void p3() {
        List<Integer> codes = Arrays.asList(200, 404, 200, 500, 201, 404, 200, 503);
        Set<Integer> s1 = new LinkedHashSet<>();
        Set<Integer> s2 = new LinkedHashSet<>();
        System.out.print("Duplicate ");
        for (int i = 0; i < codes.size(); i++) {
            if (!s1.add(codes.get(i))) {
                s2.add(codes.get(i));
            }
        }
        System.out.print(s2);


        Map<Integer, Integer> myMap = new LinkedHashMap<>();
        for (int i = 0; i < codes.size(); i++) {
            int count = 0;
            int c = codes.get(i);
            if (myMap.get(c) == null) {
                count = 0;
            } else count = myMap.get(c);
            myMap.put(c, count + 1);

        }
        System.out.println(myMap);
        for (Map.Entry<Integer, Integer> entry : myMap.entrySet()) {
            if (entry.getValue() != 1) {
                System.out.println(entry.getKey());
            }

        }

    }

    static void p4() {
        Map<String, Integer> testTimes = new HashMap<>();
        testTimes.put("loginTest", 12);
        testTimes.put("createOrderTest", 45);
        testTimes.put("getUserTest", 8);
        testTimes.put("deleteUserTest", 30);
        testTimes.put("updateProfileTest", 22);
        List<Map.Entry<String, Integer>> list = new ArrayList<>(testTimes.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<String, Integer> sortedTestTimes = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedTestTimes.put(entry.getKey(), entry.getValue());
        }

        System.out.println(sortedTestTimes);
        // mylist.sort();


    }

    static public void p5() {
        String k = "automation";
        for (int i = k.length() - 1; i >= 0; i--) {
            System.out.print(k.charAt(i));
        }
    }

    static public void p6() {
        String k = "automation";
        String r = "";
        for (int i = k.length() - 1; i >= 0; i--) {
            r = r + k.charAt(i);
        }
        if (k.equals(r)) {
            System.out.print("Pall");
        } else
            System.out.print("No pall");
    }

    static public void p7() {
        String k = "automation";

        String r = "";
        for (int i = k.length() - 1; i >= 0; i--) {
            r = r + k.charAt(i);
        }
        if (k.equals(r)) {
            System.out.print("Pall");
        } else
            System.out.print("No pall");
    }

    static public void p8() {
        String k = "automati AA  Aon";
        k = k.toLowerCase();
        String vow = "aeiou";
        int v = 0, c = 0, s = 0;
        for (int i = 0; i < k.length(); i++) {
            String p = "";
            p = p + k.charAt(i);

            if (vow.contains(p))
                v++;
            else if (p.equalsIgnoreCase(" "))
                s++;
            else
                c++;

        }
        System.out.print("Total len " + k.length() + " vow " + v + " cont" + c + " space " + s);
    }

    public static void main(String[] args) {
        p8();


    }
}

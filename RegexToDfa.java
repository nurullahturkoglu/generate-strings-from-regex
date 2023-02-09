package Automata_DFA;

import java.util.*;

public class RegexToDfa {

    private static Set<Integer>[] followPos;
    private static Node root;
    private static Set<State> DStates;

    private static Set<String> input; //set of characters is used in input regex

    /**
     * a number is assigned to each characters (even duplicate ones)
     *
     * @param symbNum is a hash map has a key which mentions the number and has
     * a value which mentions the corresponding character or sometimes a string
     * for characters is followed up by backslash like "\*"
     */
    private static HashMap<Integer, String> symbNum;

    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        Scanner in = new Scanner(System.in);
        DStates = new HashSet<>();
        input = new HashSet<String>();
        boolean isHaveKleeneStar = false;

        String language = getLanguage(in);
        String regex = getRegex(in);
        if(!checkRegexToLanguage(language,regex)){
            System.out.println("Its not matching regex with language");
            return;
        }
        getSymbols(regex);

        /**
         * giving the regex to SyntaxTree class constructor and creating the
         * syntax tree of the regular expression in it
         */
        SyntaxTree st = new SyntaxTree(regex);
        root = st.getRoot(); //root of the syntax tree
        followPos = st.getFollowPos(); //the followpos of the syntax tree


        /**
         * To get match for string
         */
        String newLanguage = newLanguage();
        int repeat = getRepeat(in);
        generateAllPossibleWord(newLanguage,repeat);
        // To get regex letters for creating permutations
        // Because we just need regex letters , not alphabet


//        String str = getWord(in);
//        isWordMatch(str,newLanguage);


//        System.out.println(possibleWords);

        in.close();
    }

    private static boolean checkRegexToLanguage(String language, String regex) {
        String regexLetters = "";
        for(int i=0;i<regex.length();i++){
            if(regex.charAt(i) == '(' || regex.charAt(i) == ')'
                    || regex.charAt(i) == '*' || regex.charAt(i) == '+'
                        || regex.charAt(i) == '|' || regex.charAt(i) == '#'){
                continue;
            }
            regexLetters += regex.charAt(i);
        }

        for(int i=0;i<regexLetters.length();i++){
            int temp_flag = 0;
            for(int j=1;j<language.length();j++){
                if(regexLetters.charAt(i) == language.charAt(j)){
                    temp_flag = 1;
                    break;
                }
            }
            if (temp_flag == 0)
                return false;

        }
        return true;
    }

    private static String getWord(Scanner in) {
        System.out.print("Please type string which do you want to check: ");
        String word = in.nextLine();
        return word;

    }
    
    private static void isWordMatch(String value,String language){

        if (checkString(value)) {
            System.out.println((char) 27 + "[32m" + "this string is acceptable by the regex!");
        } else {
            System.out.println((char) 27 + "[31m" + "this string is not acceptable by the regex!");
        }
    }


    private static void generateAllPossibleWord(String language,int repeat){
        int i=0;
        int sumOfLength = 0;
        while(true){
            if(sumOfLength == repeat){
                break;
            }
            String generatedWord = toWord(i,language);
            if(generatedWord != "" && checkString(generatedWord)){
                System.out.println(generatedWord);
                sumOfLength++;
            }
            i++;
        }

    }

    private static String newLanguage(){
        String newLanguage = "^";
        for (Iterator<String> it = input.iterator(); it.hasNext(); ) {
            String i = it.next();
            if(i.contains("#")){
                continue;
            }else{
                newLanguage += i;
            }
        }
        return newLanguage;
    }
    private static String toWord(int sayi,String alfabe){

        int alfabeLen = alfabe.length();
        String word = "";
        while(sayi > 0){
            word = word + alfabe.charAt(sayi % alfabeLen);
            sayi /= alfabeLen;
        }
        return word.contains("^") ? "" : word;
    }


    private static boolean checkString(String str){
        /**
         * creating the DFA using the syntax tree were created upside and
         * returning the start state of the resulted DFA
         */
        State q0 = createDFA();
        DfaTraversal dfat = new DfaTraversal(q0, input);

        boolean acc = false;
        /**
         * aranan kelimenin girilen alfabenin kelimelerinde oluşup oluşmadığı test ediliyor
         * daha sonra traverse ederek kelimenin alfabeden üretilip üretilemeyeceği kontrol ediliyor
         */
        for (char c : str.toCharArray()) {
            if (dfat.setCharacter(c)) {
                acc = dfat.traverse();
            } else {
                System.out.println("WRONG CHARACTER!");
                System.exit(0);
            }
        }


        if (acc) {
//            System.out.println((char) 27 + "[32m" + "this string is acceptable by the regex!\n");
            return true;
        } else {
//            System.out.println((char) 27 + "[31m" + "this string is not acceptable by the regex!\n");
            return false;
        }
    }

    private static String getRegex(Scanner in) {
        System.out.print("Enter a regex: ");
        String regex = in.nextLine();
        regex = regex.replace("+","|");
        return regex+"#";
    }

    private static void getSymbols(String regex) {
        /**
         * op is a set of characters have operational meaning for example '*'
         * could be a closure operator
         */
        Set<Character> op = new HashSet<>();
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        op.addAll(Arrays.asList(ch));

        input = new HashSet<>();
        symbNum = new HashMap<>();
        int num = 1;
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);

            /**
             * if a character which is also an operator, is followed up by
             * backslash ('\'), then we should consider it as a normal character
             * and not an operator
             */
            if (op.contains(charAt)) {
                if (i - 1 >= 0 && regex.charAt(i - 1) == '\\') {
                    input.add("\\" + charAt);
                    symbNum.put(num++, "\\" + charAt);
                }
            } else {
                input.add("" + charAt);
                symbNum.put(num++, "" + charAt);
            }
        }
    }

    private static State createDFA() {
        int id = 0;
        Set<Integer> firstpos_n0 = root.getFirstPos();

        State q0 = new State(id++);
        q0.addAllToName(firstpos_n0);
        if (q0.getName().contains(followPos.length)) {
            q0.setAccept();
        }
        DStates.clear();
        DStates.add(q0);

        while (true) {
            boolean exit = true;
            State s = null;
            for (State state : DStates) {
                if (!state.getIsMarked()) {
                    exit = false;
                    s = state;
                }
            }
            if (exit) {
                break;
            }

            if (s.getIsMarked()) {
                continue;
            }
            s.setIsMarked(true); //mark the state
            Set<Integer> name = s.getName();
            for (String a : input) {
                Set<Integer> U = new HashSet<>();
                for (int p : name) {
                    if (symbNum.get(p).equals(a)) {
                        U.addAll(followPos[p - 1]);
                    }
                }
                boolean flag = false;
                State tmp = null;
                for (State state : DStates) {
                    if (state.getName().equals(U)) {
                        tmp = state;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    State q = new State(id++);
                    q.addAllToName(U);
                    if (U.contains(followPos.length)) {
                        q.setAccept();
                    }
                    DStates.add(q);
                    tmp = q;
                }
                s.addMove(a, tmp);
            }
        }

        return q0;
    }

    private static String getLanguage(Scanner in) {
        System.out.print("Enter a language: ");
        String str;
        str = in.nextLine();
        str = "^,"+str;
        String[] strArr = str.split(",");
        String res = "";
        for(int i=0;i<strArr.length;i++){
            res += strArr[i];
        }
        return res;

    }

    private static int getRepeat(Scanner in) {
        System.out.print("How much do you want to see?: ");
        int repeat;
        repeat = in.nextInt();
        return repeat;
    }
}

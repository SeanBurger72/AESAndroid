package com.example.akntom.seguridadinformatica;

import javax.crypto.Cipher;
        import javax.crypto.KeyGenerator;
        import javax.crypto.SecretKey;
        import javax.crypto.spec.IvParameterSpec;
        import javax.crypto.spec.SecretKeySpec;

        import java.math.BigInteger;
        import java.security.MessageDigest;
        import java.security.SecureRandom;
        import java.security.spec.AlgorithmParameterSpec;
        import java.util.StringTokenizer;
        import android.util.Base64;

public class AES {

    String key , plainText , cipherText;
    String plainTextMatrix[][] ,keyMatrix[][] , actual[][] , opcional[][];
    String invsBox[][] ;
    String sbox[][];
    String rCon[];
    int mix[][];


    public AES() {}


    Cipher thecipher;
    SecretKeySpec thekey;
    AlgorithmParameterSpec spec;


    public void addKey(String password) throws Exception
    {
        // hash password with SHA-256 and crop the output to 128-bit for key
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes("UTF-8"));
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

        thecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        thekey = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }




    /*
    Algoritmo para decifrar
     */
    public void algoritmo_AESDecript(){

        //initial permutation
        actual = xorMatrixes(plainTextMatrix , keyMatrix);
        // printMatrix(actual);


        for(int i = 0 ; i < 9 ; i++) {
            System.out.println("+++++++++++++++++++++++++++++++++++++");
            //byte subtitution
            actual = subBytes(actual, invsBox);
            //   printMatrix(actual);
            //shiftrows
            actual = shiftRowsEncryptDecrypt(actual);
            // printMatrix(actual);
            //mixcolumn
            actual = mixColumns(actual);
            //   printMatrix(actual);

            //KEY
            System.out.println("Llave");
            //  printMatrix(keyMatrix);
            //
            keyMatrix = keyDecrypt(i);
            System.out.println("round 1");
            //  printMatrix(keyMatrix);
            // printMatrix(keyMatrix);

            //xor
            System.out.println("Matrix round 1");
            //  printMatrix(actual);

            actual = xorMatrixes(actual, keyMatrix);

            //
            System.out.println("Final");
            //   printMatrix(actual);

        }


        //ultimo osea 10

        //byte subtitution
        actual = subBytes(actual, invsBox);
        //printMatrix(actual);
        //shiftrows
        actual = shiftRowsEncryptDecrypt(actual);
        //printMatrix(actual);

        //KEY
        System.out.println("Llave");
        //printMatrix(keyMatrix);
        //
        keyMatrix = keyDecrypt(9);
        System.out.println("round 1");
        //printMatrix(keyMatrix);
        // printMatrix(keyMatrix);

        //xor
        System.out.println("Matrix round 1");
        //printMatrix(actual);

        actual = xorMatrixes(actual, keyMatrix);

        //
        System.out.println("Final********************************");
        printMatrix(actual);
        System.out.println("**************************************");




    }//end algoritmo_AESDecript

    /*
    Prepara la matriz
    */

    public String[][] key(int r){
        System.out.println("KEY");
        String newWordKey[][] = new String[4][4];
        String rotWordKey[] = new String[4];
        String[] matrixKeyXor = new String [4];
        String[] column = new String [4];
        String[] matrixRcon = new String [4];
        String[] medio = new String [4];


        for(int i =0 ; i < 4 ; i++){
            rotWordKey[i] = keyMatrix[i][3];
            matrixKeyXor[i] = keyMatrix[i][0];
            matrixRcon[i] = "00";
            ;
        }

        matrixRcon[0] = rCon[r];




        rotWordKey=rotWord(rotWordKey);
        rotWordKey=subBytesKey(rotWordKey, sbox);
        rotWordKey=xorMatrixesKey(rotWordKey , matrixKeyXor );
        rotWordKey=xorMatrixesKey(rotWordKey , matrixRcon );





        for(int i =0 ; i < 4 ; i++) {
            newWordKey[i][0] = rotWordKey[i];


        }


        for(int k = 1 ; k < 4 ; k++){

            matrixKeyXor = new String [4];
            column = new String [4];

            for(int i =0 ; i < 4 ; i++) {
                column[i] = keyMatrix[i][k];
                matrixKeyXor[i] = newWordKey[i][k-1];
                //System.out.println(matrixKeyXor[i]);
            }

            //guardan un punto intemedio
            medio = matrixKeyXor;


            matrixKeyXor = xorMatrixesKey(column , matrixKeyXor);
/*
            for(int i =0 ; i < 4 ; i++)
                System.out.println(column[i] +","+ medio[i]+ "--,"+matrixKeyXor[i]+"K:"+k);
*/

            //asigno
            for(int i =0 ; i < 4 ; i++) {
                newWordKey[i][k] = matrixKeyXor[i];


            }
            //printMatrix(newWordKey);
        }//end for resto



        // printMatrix(newWordKey);
        return newWordKey;

    }//end key

    public String[][] keyDecrypt(int r){
        System.out.println("KEY");
        String newWordKey[][] = new String[4][4];
        String rotWordKey[] = new String[4];
        String[] matrixKeyXor = new String [4];
        String[] column = new String [4];
        String[] matrixRcon = new String [4];
        String[] medio = new String [4];


        for(int i =0 ; i < 4 ; i++){
            rotWordKey[i] = keyMatrix[i][3];
            matrixKeyXor[i] = keyMatrix[i][0];
            matrixRcon[i] = "00";

        }

        matrixRcon[0] = rCon[r];


        rotWordKey =rotWord(rotWordKey);
        rotWordKey=subBytesKey(rotWordKey, invsBox);
        rotWordKey=xorMatrixesKey(rotWordKey , matrixKeyXor );
        rotWordKey=xorMatrixesKey(rotWordKey , matrixRcon );





        for(int i =0 ; i < 4 ; i++) {
            newWordKey[i][0] = rotWordKey[i];


        }


        for(int k = 1 ; k < 4 ; k++){

            matrixKeyXor = new String [4];
            column = new String [4];

            for(int i =0 ; i < 4 ; i++) {
                column[i] = keyMatrix[i][k];
                matrixKeyXor[i] = newWordKey[i][k-1];
                //System.out.println(matrixKeyXor[i]);
            }

            //guardan un punto intemedio
            medio = matrixKeyXor;


            matrixKeyXor = xorMatrixesKey(column , matrixKeyXor);
/*
            for(int i =0 ; i < 4 ; i++)
                System.out.println(column[i] +","+ medio[i]+ "--,"+matrixKeyXor[i]+"K:"+k);
*/

            //asigno
            for(int i =0 ; i < 4 ; i++) {
                newWordKey[i][k] = matrixKeyXor[i];


            }
            //printMatrix(newWordKey);
        }//end for resto



        // printMatrix(newWordKey);
        return newWordKey;

    }//end key

    public String[][] convert_State(String text){
        System.out.println("convert_State");

        //variables
        StringTokenizer tokenizer;
        String matrix[][];
        //   HexBinaryAdapter adapter = new HexBinaryAdapter();
        //elimino los espacion en blaco
        text = text.trim();
        System.out.println(text);
        //tokennizer
        tokenizer = new StringTokenizer(text," ");
        //inicio matrix
        matrix = new String[4][4];

        //leno la matriz y la imprimo
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix.length; j++)
            {
                if(tokenizer.hasMoreTokens())
                    matrix[i][j] = tokenizer.nextToken();

            }//end ford

        printMatrix(matrix);
        //regreso la matrix
        return matrix;






    }//end convert_State

    public String[][] subBytes(String[][] unaMatrix , String[][] unaCaja){
        System.out.println("SubBytes");
        String derecha , arriba , nuevaMatrix[][];
        int derechaInt , arribaInt;
        //inicializo la matrix
        nuevaMatrix = new String[4][4];

        for (int i = 0; i < unaMatrix.length; i++)
            for (int j = 0; j < unaMatrix.length; j++){
                derecha = unaMatrix[i][j].substring(0,1);
                arriba = unaMatrix[i][j].substring(1,2);

                derechaInt = hexToInt(derecha);
                arribaInt =  hexToInt(arriba);

                nuevaMatrix[i][j]=unaCaja[derechaInt][arribaInt];
            }//end for
        return nuevaMatrix;



    }//end subytes

    public String[][] shiftRowsEncrypt(String[][] unaMatrix){
        System.out.println("shiftRowsEncrypt");
        String nuevaMatrix[][];
        nuevaMatrix = new String[4][4];

        for (int i = 0; i < unaMatrix.length; i++)
            for (int j = 0; j < unaMatrix.length; j++){

                nuevaMatrix[i][j] = unaMatrix[i][(j+i) % 4];
            }
        return nuevaMatrix;

    }//end shiftRows


    public String[][] shiftRowsEncryptDecrypt(String[][] unaMatrix){
        System.out.println("shiftRowsEncryptDecrypt");
        String nuevaMatrix[][];
        nuevaMatrix = new String[4][4];

        for (int i = 0; i < unaMatrix.length; i++)
            for (int j = 0; j < unaMatrix.length; j++){

                nuevaMatrix[i][(j+i) % 4] = unaMatrix[i][j];
                // System.out.println();

            }
        return nuevaMatrix;

    }//end shiftRows

    public String[][] mixColumns(String[][] unaMatrix){
        System.out.println("mixColumns");

        String vertical[], resultadoMatrix[][];
        int horizontal[];

        vertical = new String[4];
        horizontal = new int[4];
        resultadoMatrix = new  String[4][4];

        /*


        for (int k = 0; k < 4; k++)
                for (int j = 0; j < 4; j++)
                    for (int i = 0; i < 4; i++) {
                        vertical[i] = unaMatrix[i][j];
                        horizontal[i] = mix[j][i];
                        System.out.println(vertical[i]);
                        System.out.println(horizontal[i]);
                        resultadoMatrix[k][i] = mixColumsMulti(vertical , horizontal);

                    }//end for de partes

*/


        for (int y=0; y < 4; y++) {
            for (int z=0; z<4; z++) {
                // System.out.println( "Y:"+y +"Z:"+z+ "---------" +"["+(y)+" ,"+z+" ] = "+y+","+z);
                //for (int k = 0; k < 4; k++) {

                for (int i = 0; i < 4; i++) {

                    vertical[i] = unaMatrix[i][y];
                    horizontal[i] = mix[z][i];
                }



                //}
                resultadoMatrix[z][y] = mixColumsMulti(vertical, horizontal);
                // System.out.println(resultadoMatrix[z][y]);
                // printMatrix(resultadoMatrix);
            }
        }




//        resultadoMatrix[0][0] = mixColumsMulti(vertical, horizontal) ;



        return resultadoMatrix;


    }//end mix

    public String mixColumsMulti(String[] vertical , int[] horizontal){

        String resultado="";

        for(int j = 0 ; j < 4 ; j++)
        {
            //   System.out.print(vertical[j]);
            //  System.out.print(horizontal[j]);
        }
        // System.out.println( );

        for(int i = 0 ; i < 4 ; i++)
        {
            String producto="";
            // System.out.println(horizontal[i]);
            if(horizontal[i] ==1){
                producto = vertical[i];
                // System.out.println("1:"+ producto);
            }
            if(horizontal[i] ==2){

                String hex = significativeByte(vertical[i]);
                producto = hex;


            }//end 2

            if(horizontal[i] ==3){

                String hex = significativeByte(vertical[i]);

                String hexOr = xorNumbers(vertical[i] , hex);
                producto = hexOr;
            }//end 3

            // System.out.println("-"+producto);

            if(i == 0){
                resultado = producto;

            }//end if
            else
            {

                //  System.out.println("R:"+resultado + "p:"+producto);

                resultado = xorNumbers(resultado , producto);

            } //end else



        }//end for
        //System.out.println("--------------------------------"+resultado);
        return resultado;

    }//end mixColumsMulti

    public String[][] xorMatrixes(String[][] derecha , String[][] izquierda){
        System.out.println("xorMatrixes");

        String resultMatrix[][];
        resultMatrix = new String[4][4];

        for (int i = 0; i < derecha.length; i++)
            for (int j = 0; j < derecha.length; j++) {

                resultMatrix[i][j] = xorNumbers(derecha[i][j] , izquierda[i][j]);



            }//end fors

        return resultMatrix;
    }//end xorMatrixes

    public void printMatrix(String[][] matrix ){

        for (int i = 0; i < matrix.length; i++) {
            if(i>0)
                System.out.println();
            for (int j = 0; j < matrix.length; j++) {
                if(j<matrix.length-1)
                    System.out.print(matrix[i][j] + "-");
                else
                    System.out.print(matrix[i][j] );
            }
        }
        System.out.println();
    }//end printMatrix

    public String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }



    /////////////////////////

    public AlgorithmParameterSpec getIV()
    {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);

        return ivParameterSpec;
    }

    public String encryptComplete(String plainText) throws Exception
    {
        thecipher.init(Cipher.ENCRYPT_MODE, thekey, spec);
        byte[] encrypted = thecipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedText = new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");

        return encryptedText;
    }

    public String decryptComplete(String cryptedText) throws Exception
    {
        thecipher.init(Cipher.DECRYPT_MODE, thekey, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = thecipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");

        return decryptedText;
    }

    ////////////////////////

    private String hexToBin(String hex){
        String bin = "";
        String binFragment = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for(int i = 0; i < hex.length(); i++){
            iHex = Integer.parseInt(""+hex.charAt(i),16);
            binFragment = Integer.toBinaryString(iHex);

            while(binFragment.length() < 4){
                binFragment = "0" + binFragment;
            }
            bin += binFragment;
        }
        return bin;
    }

    private String binToHex(String bin){
        int i = Integer.parseInt(bin , 2);
        return Integer.toHexString(i);
    }

    private int hexToInt(String hex){
        String bin = hexToBin(hex);
        return Integer.parseInt(bin, 2);

    }

    private String xorNumbers(String a , String b){


        String numberA , numberB, numberR="";
        int intA , intB , intR;
        a = hexToBin(a);
        b = hexToBin(b);

        if(a.length() == b.length()){
            for(int i = 0 ; i < a.length() ; i++) {
                numberA = a.substring(i , i+1);
                numberB = b.substring(i , i+1);
                intA = Integer.parseInt(numberA);
                intB = Integer.parseInt(numberB);
                if(intA == intB)
                    intR = 0;
                else
                    intR = 1;
                numberR = numberR + Integer.toString(intR);
            }//end for
        }//end if
        //si es de un solo digito
        numberR = binToHex(numberR);
        if(numberR.length()==1)
            numberR = "0"+numberR;
        return numberR;
    }//end xorNumbers




    public  String cipherAES( String type , String keyHex , String plainText) throws Exception {
        // final String keyHex = key;

        final String plaintextHex = plainText;



        byte[] data = keyHex.getBytes();
        SecretKey key = new SecretKeySpec(data, "AES");
        //  SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(keyHex), "AES");



        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

        if(type.compareTo("EN")==0)
            cipher.init(Cipher.ENCRYPT_MODE, key);
        if(type.compareTo("DE")==0)
            cipher.init(Cipher.DECRYPT_MODE, key);

        // byte[] result = cipher.doFinal(DatatypeConverter.parseHexBinary(plaintextHex));
        byte[] result = cipher.doFinal(plaintextHex.getBytes());




        // System.out.println(DatatypeConverter.printHexBinary(result));
        return result.toString();


        // System.out.println(DatatypeConverter.printHexBinary(result));
        // return CharSequenceresult.toString();
    }

    private String xorNumbersMix(String a , String b){

        String numberA , numberB, numberR="";
        int intA , intB , intR;
        a = hexToBin(a);
        b = hexToBin(b);
        if(a.length() == b.length()){
            for(int i = 0 ; i < a.length() ; i++) {
                numberA = a.substring(i , i+1);
                numberB = b.substring(i , i+1);
                intA = Integer.parseInt(numberA);
                intB = Integer.parseInt(numberB);
                if(intA == intB)
                    intR = 0;
                else
                    intR = 1;
                numberR = numberR + Integer.toString(intR);
            }//end for
        }//end if
        //si es de un solo digito
        numberR = binToHex(numberR);
        if(numberR.length()==1)
            numberR = "0"+numberR;
        return numberR;
    }//end xorNumbers


    public String significativeByte(String hex){
        // System.out.println("significativeByte");
        String binShiftByte, resultado , ceros , byteSignificative;
        ceros ="00000000";
        String bin = hexToBin(hex);

        byteSignificative = bin.substring(0,1);
        // System.out.println("%"+byteSignificative);
        binShiftByte = bin.substring(1,bin.length()) + "0";

        // System.out.println("Significative:" + binShiftByte);

        resultado = binToHex(binShiftByte);
        if(resultado.length()==1)
            resultado = "0"+resultado;
        if(byteSignificative.compareTo("1")==0)
            resultado = xorNumbers(resultado , "1B");

        // resultado =resultado.toUpperCase();
        return resultado;
    }

    public void boxCreate() {




        String[][] aboxUno = {{"52","09","6A","D5","30","36","A5","38","BF","40","A3","9E","81","F3","D7","FB",},
                {"7C","E3","39","82","9B","2F","FF","87","34","8E","43","44","C4","DE","E9","CB",},
                {"54","7B","94","32","A6","C2","23","3D","EE","4C","95","0B","42","FA","C3","4E",},
                {"08","2E","A1","66","28","D9","24","B2","76","5B","A2","49","6D","8B","D1","25",},
                {"72","F8","F6","64","86","68","98","16","D4","A4","5C","CC","5D","65","B6","92",},
                {"6C","70","48","50","FD","ED","B9","DA","5E","15","46","57","A7","8D","9D","84",},
                {"90","D8","AB","00","8C","BC","D3","0A","F7","E4","58","05","B8","B3","45","06",},
                {"D0","2C","1E","8F","CA","3F","0F","02","C1","AF","BD","03","01","13","8A","6B",},
                {"3A","91","11","41","4F","67","DC","EA","97","F2","CF","CE","F0","B4","E6","73",},
                {"96","AC","74","22","E7","AD","35","85","E2","F9","37","E8","1C","75","DF","6E",},
                {"47","F1","1A","71","1D","29","C5","89","6F","B7","62","0E","AA","18","BE","1B",},
                {"FC","56","3E","4B","C6","D2","79","20","9A","DB","C0","FE","78","CD","5A","F4",},
                {"1F","DD","A8","33","88","07","C7","31","B1","12","10","59","27","80","EC","5F",},
                {"60","51","7F","A9","19","B5","4A","0D","2D","E5","7A","9F","93","C9","9C","EF",},
                {"A0","E0","3B","4D","AE","2A","F5","B0","C8","EB","BB","3C","83","53","99","61",},
                {"17","2B","04","7E","BA","77","D6","26","E1","69","14","63","55","21","0C","7D",}};



        String[][] aboxDos = {{"63","7C","77","7B","F2","6B","6F","C5","30","01","67","2B","FE","D7","AB","76",},
                {"CA","82","C9","7D","FA","59","47","F0","AD","D4","A2","AF","9C","A4","72","C0",},
                {"B7","FD","93","26","36","3F","F7","CC","34","A5","E5","F1","71","D8","31","15",},
                {"04","C7","23","C3","18","96","05","9A","07","12","80","E2","EB","27","B2","75",},
                {"09","83","2C","1A","1B","6E","5A","A0","52","3B","D6","B3","29","E3","2F","84",},
                {"53","D1","00","ED","20","FC","B1","5B","6A","CB","BE","39","4A","4C","58","CF",},
                {"D0","EF","AA","FB","43","4D","33","85","45","F9","02","7F","50","3C","9F","A8",},
                {"51","A3","40","8F","92","9D","38","F5","BC","B6","DA","21","10","FF","F3","D2",},
                {"CD","0C","13","EC","5F","97","44","17","C4","A7","7E","3D","64","5D","19","73",},
                {"60","81","4F","DC","22","2A","90","88","46","EE","B8","14","DE","5E","0B","DB",},
                {"E0","32","3A","0A","49","06","24","5C","C2","D3","AC","62","91","95","E4","79",},
                {"E7","C8","37","6D","8D","D5","4E","A9","6C","56","F4","EA","65","7A","AE","08",},
                {"BA","78","25","2E","1C","A6","B4","C6","E8","DD","74","1F","4B","BD","8B","8A",},
                {"70","3E","B5","66","48","03","F6","0E","61","35","57","B9","86","C1","1D","9E",},
                {"E1","F8","98","11","69","D9","8E","94","9B","1E","87","E9","CE","55","28","DF",},
                {"8C","A1","89","0D","BF","E6","42","68","41","99","2D","0F","B0","54","BB","16",}};


        mix[0][0] =2;
        mix[0][1] =3;
        mix[0][2] =1;
        mix[0][3] =1;

        mix[1][0] =1;
        mix[1][1] =2;
        mix[1][2] =3;
        mix[1][3] =1;

        mix[2][0] =1;
        mix[2][1] =1;
        mix[2][2] =2;
        mix[2][3] =3;

        mix[3][0] =3;
        mix[3][1] =1;
        mix[3][2] =1;
        mix[3][3] =2;


        opcional[0][0] ="87";
        opcional[0][1] ="F2";
        opcional[0][2] ="4D";
        opcional[0][3] ="97";

        opcional[1][0] ="6E";
        opcional[1][1] ="4C";
        opcional[1][2] ="90";
        opcional[1][3] ="EC";

        opcional[2][0] ="46";
        opcional[2][1] ="E7";
        opcional[2][2] ="4A";
        opcional[2][3] ="C3";

        opcional[3][0] ="A6";
        opcional[3][1] ="8C";
        opcional[3][2] ="D8";
        opcional[3][3] ="95";


        String[] con = {"01","02","04","08", "10","20","40","80","1b","36"};

        rCon = con;
        sbox = aboxDos;
        invsBox = aboxUno;


        System.out.println(aboxUno[0][0]);
        System.out.println(aboxDos[0][0]);




    }

    public void arreglar(String cadena){


        StringTokenizer tokenizer = new StringTokenizer(cadena);
        String resultado="{";
        String com = Character.toString ((char) 34);

        while (tokenizer.hasMoreTokens()){
            String unToken = tokenizer.nextToken();
            resultado = resultado + com +unToken.substring(2 , 4) + com + ",";
        }
        resultado = resultado + "},";
        System.out.println(resultado);
    }

    public String multiPLayTwo(String hex ){

        int n = hexToInt(hex);
        int r = n * 2;
        String bin = Integer.toBinaryString(r);
        return bin;
    }//end multiPLayTwo

    public String[] rotWord(String word[]){
        String secondWord[] ;
        secondWord = new String[4];


        secondWord[0] = word[1];
        secondWord[1] = word[2];
        secondWord[2] = word[3];
        secondWord[3] = word[0];

        return secondWord;

    }//end rotWord

    public String[] subBytesKey(String[] unaMatrix , String[][] unaCaja){
        System.out.println("SubBytes");
        String derecha , arriba , nuevaMatrix[];
        int derechaInt , arribaInt;
        //inicializo la matrix
        nuevaMatrix = new String[4];

        for (int j = 0; j < unaMatrix.length ;j++){
            derecha = unaMatrix[j].substring(0,1);
            arriba = unaMatrix[j].substring(1,2);


            derechaInt = hexToInt(derecha);
            arribaInt =  hexToInt(arriba);


            nuevaMatrix[j]=unaCaja[derechaInt][arribaInt];
        }//end for
        return nuevaMatrix;



    }//end subytes


    public String[] xorMatrixesKey(String[] derecha , String[] izquierda){
        //System.out.println("xorMatrixes");

        String resultMatrix[];
        resultMatrix = new String[4];


        for (int j = 0; j < derecha.length; j++) {

            resultMatrix[j] = xorNumbers(derecha[j] , izquierda[j]);


        }//end fors

        return resultMatrix;
    }//end xorMatrixes

    /////metodos para desencriptar







}//end class
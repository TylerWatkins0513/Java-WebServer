import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;






public class MyWebServer {
    private static ServerSocket serverSocket;
    private static int port;

    public static void main(String[] args) throws IOException {
        port = 8888;
        //Make new socket 
        serverSocket = new ServerSocket(port);
        System.out.println("The server is ready to receive on port " + port + "\n");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread clientThread = new Thread(new ClientHandler(clientSocket));
            clientThread.start();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                String line = null;
                Date requestedDate = new Date();
                
                String response = "";
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               String firstLine=socketInput.readLine();
               String [] fLineArray = firstLine.split(" ");
               String newFile = fLineArray[1];
                newFile = '.' + newFile;
                File file_path = new File(newFile);
                long last_modified = file_path.lastModified();
                String modified_response = "";

                if(!file_path.isFile()){
                    modified_response = "404 Not Found";
               }

               if (fLineArray[0].equals("GET") || fLineArray[0].equals("HEAD")){
                 response  = "HTTP/1.1 200 OK";
               } else {
                response = "HTTP/1.1 501 NOT IMPLEMENTED";
               }
               

                System.out.println(fLineArray[0] + " " + fLineArray [1]+ " ");

               //Start read
                while( (line=socketInput.readLine()) != null && !line.equals("")){
                    System.out.println(line);
                    
                        
                     }
                      
                
                
                //Response for valid get request
                PrintWriter socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);

               

                if(fLineArray[0].equals("GET")){
                responses(file_path,requestedDate,socketOutput);
                printFile(newFile,socketOutput);
                } if(fLineArray[0].equals("HEAD")){
                responses(file_path, requestedDate, socketOutput);
                }

                System.out.println(modified_response);
                System.out.println(response);
                System.out.println();
                socketOutput.println(response);
                
                socketOutput.println(fLineArray[0] + " " + fLineArray [1]+ " ");


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public static void responses( File file, Date Date, PrintWriter socketOutput){
            long fileSize = file.length();
            socketOutput.println("HTTP/1.1 200 OK");
            socketOutput.println("Date: " + Date);
            socketOutput.println("Connection: keep-alive");
            socketOutput.println("Server: MyWebServer");
            socketOutput.println("Content-Length: " + fileSize);
            socketOutput.println();
            socketOutput.flush(); 

            System.out.println("HTTP/1.1 200 OK");
            System.out.println("Date: " + Date);
            System.out.println("Connection: keep-alive");
            System.out.println("Server: MyWebServer");
            System.out.println("Content-Length: " + fileSize);
            System.out.println();

    }
    public static void printFile(String newFile, PrintWriter socketOupt){
        try (BufferedReader reader = new BufferedReader(new FileReader(newFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                socketOupt.println(line);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
    }
        
    }
    }
}

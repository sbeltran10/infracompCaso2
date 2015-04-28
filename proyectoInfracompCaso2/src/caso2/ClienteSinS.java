package caso2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Date;

public class ClienteSinS {

	//------------------------------------------------
		// Constantes
		//------------------------------------------------

		/**
		 * Direccion del servidor a usar.
		 */
		private final static String SERV = "192.168.0.8";

		/**
		 * Puerto servidor con seguridad
		 */
		private final static int PORT = 90;

		/**
		 * Puerto del servidor sin autenticaciones de seguridad
		 */
		private final static int PORTINSEGUR = 80;

		/**
		 * Cadena de control que indica el inicio de la conversacion
		 */
		private final static String HOLA = "HOLA";

		/**
		 * Cadena de control que indica
		 */
		private final static String ALGORITMOS = "ALGORITMOS";

		/**
		 * Cadena de control que indica una conexion exitosa
		 */
		private final static String OK = "OK";

		/**
		 * Cadena de control que indica el envio del certificado del cliente
		 */
		private final static String CERCLNT = "CERCLNT";
		
		/**
		 * Actualizacion 1 de pocicsion
		 */
		private final static String ACT1 = "ACT1";
		
		/**
		 * 
		 */
		private final static String ACT2 = "ACT2";

		//-------------------------------------------------
		// Atributos
		//-------------------------------------------------

		/**
		 * El socket para realizar la conexion al servidor
		 */
		private Socket socket;

		/**
		 * Writer para enviar mensajes de control al servidor
		 */
		private PrintWriter out;

		/**
		 * Reader para leer los mensajes de control enviados por el servidor
		 */
		private BufferedReader in;

		/**
		 * Mide el tiempo de establecimiento de llave de sesion
		 */
		private static long tILlaveSesion;
		
		/**
		 * Mide el tiempo de establecimiento de llave de sesion
		 */
		private static long tFLlaveSesion;
		
		/**
		 * Mide el tiempo de la transaccion
		 */
		private static long tITransaccion;
		
		/**
		 * Mide el tiempo de la transaccion
		 */
		private static long tFTransaccion;
		
		/**
		 * Indica si la transaccion fue exitosa
		 */
		private static boolean exitosa;
		
		/**
		 * Indica el tiempo que tomo antes de que fallara la transaccion
		 */
		private static long tFallo;
		
		private long tFalloLllave;
		
		//-----------------------------------------------
		// Constructor
		//-----------------------------------------------

		/**
		 * 
		 * @param port
		 * @throws Exception
		 */
		public ClienteSinS(int port) {
			try{

				socket = new Socket(SERV, port);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			} catch(Exception e){ 
				e.printStackTrace();
				
				}
		}

		/**
		 * Se inicia la comunicacion con el servidor enviando la cadena de control "HOLA"
		 */
		public boolean establecerConexion() {
			try {
				tITransaccion = System.currentTimeMillis();
				System.out.println("Cliente: " + HOLA);
				out.println(HOLA);
				System.out.println("Servidor: " + in.readLine());

			} catch (IOException e) {
				e.printStackTrace();
				tFallo = System.currentTimeMillis();
				exitosa = false;
				return false;
			}
			return true;
		}

		/**
		 * Manda los algoritmos que seran usados al servidor
		 */
		public boolean mandarAlgoritmos(String algos, String algoa, String algod){

			try{
				System.out.println("Cliente: " + ALGORITMOS+":"+algos+":"+algoa+":"+algod);
				out.println(ALGORITMOS+":"+algos+":"+algoa+":"+algod);

				String respuesta = in.readLine();

				System.out.println("Servidor: " + respuesta);
				String estado = respuesta.split(":")[1];

				if(estado.equals(OK)){return true;}
				else{return false;}
			}

			catch(Exception e){ 
				e.printStackTrace();
				tFallo = System.currentTimeMillis();
				exitosa = false;
				return false; 
			}
		}

		/**
		 * Envia el certificado al servidor por un flujo de bytes
		 */
		public byte[] envioCertificado(String algAsim) {
			try {
				out.println(CERCLNT);
				socket.getOutputStream().write("CertificadoCli".getBytes());
				socket.getOutputStream().flush();	
				tILlaveSesion = System.currentTimeMillis();
				
				return "CertificadoCli".getBytes();

			} catch (Exception e) {
				e.printStackTrace();
				tFallo = System.currentTimeMillis();
				exitosa = false;
				return null;
			}
		}

//		/**
//		 * Crea el certificado digital del cliente
//		 * @return
//		 */
//		private X509Certificate crearCertificado(String algAsim){
//			
//			Date startDate = new Date(System.currentTimeMillis());                
//			Date expiryDate = new Date(System.currentTimeMillis() + 30L * 365L * 24L * 60L * 60L * 1000L);
//			BigInteger serialNumber = new BigInteger("26");       
//
//			KeyPair llavesCliente = generarLlave(algAsim);
//			PrivateKey caKey = llavesCliente.getPrivate();              
//			X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
//			X500Principal  subjectName = new X500Principal("CN=Test V3 Certificate"); 
//			certGen.setSerialNumber(serialNumber);
//			certGen.setIssuerDN(subjectName);
//			certGen.setNotBefore(startDate);
//			certGen.setNotAfter(expiryDate);
//			certGen.setSubjectDN(subjectName);
//			certGen.setPublicKey(llavesCliente.getPublic());
//			certGen.setSignatureAlgorithm("SHA256WithRSAEncryption"); 
//
//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//
//			try {
//				X509Certificate cert = certGen.generate(caKey, "BC"); 
//
//				System.out.println("Cliente: "+CERCLNT);
//				System.out.println("------------------------------------------------------------------");
//				System.out.println(cert.toString());
//				System.out.println("------------------------------------------------------------------");
//
//				return cert;
//			} catch (Exception e) {
//				e.printStackTrace();
//				tFallo = System.currentTimeMillis();
//				exitosa = false;
//				return null;
//			}   
//		}

//		/**
//		 * Genera la llave privada y publica del cliente
//		 * @return
//		 */
//		private KeyPair generarLlave(String algAsim){		
//			KeyPairGenerator generator;
//			try {
//				generator = KeyPairGenerator.getInstance(algAsim);
//				generator.initialize(1024);
//				llavesCliente = generator.generateKeyPair();
//				return llavesCliente;
//			} catch (NoSuchAlgorithmException e) {
//				e.printStackTrace();
//				tFallo = System.currentTimeMillis();
//				exitosa = false;
//				return null;
//			}
//		}

//		/**
//		 * Se recibe el certificado del servidor
//		 * @return
//		 */
//		public PublicKey recibirCertificadoServidor(){
//			try {
//				System.out.println("Servidor: " +  in.readLine());
//				CertificateFactory  cf = CertificateFactory.getInstance("X.509");
//				Certificate certificate = cf.generateCertificate(socket.getInputStream());getClass();
//				
//				System.out.println("------------------------------------------------------------------");
//				System.out.println(certificate.toString());
//				System.out.println("------------------------------------------------------------------");
//
//				System.out.println("Llave publica servidor: " + certificate.getPublicKey());
//				System.out.println();
//				return certificate.getPublicKey();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				tFallo = System.currentTimeMillis();
//				tFalloLllave = tFallo;
//				exitosa = false;
//				return null;
//			}
//		}

//		/**
//		 * Extrae la llave simetrica enviada por el servidor
//		 * @return
//		 */
//		public SecretKey extraerLlavesimetrica(String algSimetrico, String algAsimetrico){
//
//			try{
//
//				String[] llaveSimInit;
//				try {
//					llaveSimInit = in.readLine().split(":");
//					tFLlaveSesion = System.currentTimeMillis();
//					
//					System.out.println("Servidor: " + llaveSimInit[0] + ": " + llaveSimInit[1]);
//					
//					Cipher cipher = Cipher.getInstance(algAsimetrico);
//					cipher.init(Cipher.DECRYPT_MODE, llavesCliente.getPrivate());
//					
//					
//					byte[] llaveSimetricaCif = DatatypeConverter.parseHexBinary(llaveSimInit[1]);
//					byte[] decifrado = cipher.doFinal(llaveSimetricaCif);
//					String llaveSimetrica = new String(decifrado);
//					
//					System.out.println("Llave simetrica: " + llaveSimetrica);
//					SecretKey simetrica = new SecretKeySpec(decifrado,0,decifrado.length,algSimetrico);
//					
//					return simetrica;
//				} catch (IOException e) {
//					e.printStackTrace();
//					tFallo = System.currentTimeMillis();
//					tFalloLllave = tFallo;
//					exitosa = false;
//					return null;
//				} catch (Exception e) {
//					e.printStackTrace();
//					tFallo = System.currentTimeMillis();
//					exitosa = false;
//				} 
//			return null;
//		}
		
//		private String encapsular(byte[] cifrado){
//			char[] hexArray = "0123456789ABCDEF".toCharArray();
//			char[] hexChars = new char[cifrado.length * 2];
//		    for ( int j = 0; j < cifrado.length; j++ ) {
//		        int v = cifrado[j] & 0xFF;
//		        hexChars[j * 2] = hexArray[v >>> 4];
//		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//		    }
//		    return new String(hexChars);
//		}

		/**
		 * Envia la pocision actualizada al servidor usando la llave simetrica obtenidad anteriormente
		 * para cifrar dicha pocision.
		 */
		public boolean actualizarUbicacion(String ubicacion) {

			try {

				in.readLine();
				in.readLine();
				//===================================
				// ACT1
				//===================================
				
				System.out.println("Posicion Actual: " + ubicacion);
				
				out.println(ACT1);
				

				//===================================
				// ACT2
				//==================================
				
				out.println(ACT2);
				
				
				System.out.println("Servidor: " + in.readLine());
				tFTransaccion = System.currentTimeMillis();
				return true;

			} catch (Exception e) {
				e.printStackTrace();
				tFallo = System.currentTimeMillis();
				exitosa = false;
				return false;
			}
		}

		/**
		 * Main
		 * @param args
		 */
		public static void main(String[] args){
			ClienteSinS cli = null;
			exitosa = true;
			
			try{
				cli = new ClienteSinS(PORT);
			}catch(Exception e){
				e.printStackTrace();
				exitosa = false;
			}

			//Manejo de diferentes casos de algoritmos

			String algSimetrico ="";
			String algAsimetrico = "";
			String algHmac = "";
			String paddingSim = "";

			try{
				BufferedReader br = new BufferedReader(new FileReader("data/RC4_RSA_HMACSHA256"));

				algSimetrico = br.readLine().split(":")[1];
				algAsimetrico = br.readLine().split(":")[1];
				algHmac = br.readLine().split(":")[1];
				paddingSim = br.readLine().split(":")[1];
				br.close();

			} catch(Exception e){ 
				e.printStackTrace();
				tFallo = System.currentTimeMillis();
				exitosa = false;
				e.printStackTrace();


				}

			//comienzo de la comunicacion cliente a servidor
			
			cli.establecerConexion();

			boolean algosAceptados = cli.mandarAlgoritmos(algSimetrico, algAsimetrico, algHmac);

			if(!algosAceptados){
				System.out.println("No se aceptaron los algoritmos");
				exitosa = false;
			}
			else{
				cli.envioCertificado(algAsimetrico);

				cli.actualizarUbicacion("41242028,2104418");
			}
			
		}
		
		public long darTiempoLlaveSesion(){
			return tFLlaveSesion - tILlaveSesion;
		}
		
		public long darTimepoTransaccion(){
			return tFTransaccion - tITransaccion;
		}
		
		public long darTiempoFallo(){
			return tFallo - tITransaccion;
		}
		
		public long darTiempoFalloLlave(){
			return tFalloLllave - tILlaveSesion;
		}
		
		public boolean darTransaccionExitosa(){
			return exitosa;
		}

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}

		public PrintWriter getOut() {
			return out;
		}

		public void setOut(PrintWriter out) {
			this.out = out;
		}

		public BufferedReader getIn() {
			return in;
		}

		public void setIn(BufferedReader in) {
			this.in = in;
		}

		public static long gettILlaveSesion() {
			return tILlaveSesion;
		}

		public static long gettFLlaveSesion() {
			return tFLlaveSesion;
		}

		public static long gettITransaccion() {
			return tITransaccion;
		}

		public static long gettFTransaccion() {
			return tFTransaccion;
		}

		public long gettFallo() {
			return tFallo;
		}

		public void settFallo(long tFallo) {
			this.tFallo = tFallo;
		}

		public long gettFalloLllave() {
			return tFalloLllave;
		}

		public void settFalloLllave(long tFalloLllave) {
			this.tFalloLllave = tFalloLllave;
		}
		
	}



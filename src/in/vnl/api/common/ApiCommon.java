package in.vnl.api.common;

import in.vnl.EventProcess.DBDataService;
import in.vnl.api.common.livescreens.AlarmDataServer;
import in.vnl.api.common.livescreens.AutoOperationServer;
import in.vnl.api.common.livescreens.AutoStateServer;
import in.vnl.api.common.livescreens.DeviceStatusServer;
import in.vnl.api.common.livescreens.ScanTrackModeServer;
import in.vnl.api.config.PossibleConfigurations;
import in.vnl.api.fourg.FourgOperations;
import in.vnl.api.netscan.CurrentNetscanAlarm;
import in.vnl.api.netscan.NetscanOperations;
import in.vnl.api.netscan.NetscanService;
import in.vnl.api.netscan.livescreens.NetScanAlarmServer;
import in.vnl.api.threeg.ThreegOperations;
import in.vnl.api.threeg.livescreens.AlarmServer;
import in.vnl.api.twog.TwogOperations;
import in.vnl.msgapp.Common;
import in.vnl.msgapp.Operations;
import in.vnl.sockets.UdpServerClient;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
//import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import javax.ws.rs.FormParam;
//import java.util.Calendar;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse.Builder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;

import javax.servlet.http.HttpServletRequest;



public class ApiCommon 
{
   static Logger fileLogger = Logger.getLogger("file");
   static Logger statusLogger = Logger.getLogger("status");
   static Operations operations=new Operations();
   public synchronized Response sendRequestToUrl(String url,LinkedHashMap<String,String> queryParam,String data)
   {
	   fileLogger.info("Inside Function : sendRequestToUrl");
	   Response res = null;
	   try
	   {
	  /// fileLogger.debug("**********sendRequestToUrl**********");
	   fileLogger.debug("url is :"+url);
	   //fileLogger.debug("**********sendRequestToUrl**********");
		   Client client = ClientBuilder.newClient();
		   client.property(ClientProperties.CONNECT_TIMEOUT,  60000);
		   client.property(ClientProperties.READ_TIMEOUT,    120000);
		   
		   
		   WebTarget webTarget = client.target(url);
		   if(queryParam != null) 
		   {
			   for(String key:queryParam.keySet())
			   {
				   webTarget=webTarget.queryParam(key, queryParam.get(key));
			   }
		   }
		   
		   
		    res =   webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(data)); 
		//	fileLogger.debug("**********Response Received**********");
			   fileLogger.debug("url is :"+url);
			//   fileLogger.debug("**********Response Received**********");
	   }catch(Exception e)
	   {
		   fileLogger.error("Exception while making request : MSG : "+e.getMessage());
		   throw e;
	   }
	   fileLogger.info("Exit Function : sendRequestToUrl");
	   return res;
	   
   }
   
   public synchronized Response sendRequestToUrl(String url,LinkedHashMap<String,String> queryParam)
   {
	   fileLogger.info("Inside Function : sendRequestToUrl");
	   Response res = null;
	   try
	   {
	  // fileLogger.debug("**********sendRequestToUrl**********");
	   fileLogger.debug("url is :"+url);
	  // fileLogger.debug("**********sendRequestToUrl**********");
		   Client client = ClientBuilder.newClient();
		   client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
		   client.property(ClientProperties.READ_TIMEOUT,    60000);
		   
		   
		   WebTarget webTarget = client.target(url);
		   if(queryParam != null) 
		   {
			   for(String key:queryParam.keySet())
			   {
				   webTarget=webTarget.queryParam(key, queryParam.get(key));
			   }
		   }
		   
		   
		    res =   webTarget.request(MediaType.APPLICATION_JSON).get(); 
			//fileLogger.debug("**********Response Received**********");
			   fileLogger.debug("url is :"+url);
			//   fileLogger.debug("**********Response Received**********");
	   }catch(Exception e)
	   {
		   fileLogger.error("Exception while making request : MSG : "+e.getMessage());
		   throw e;
	   }
	   fileLogger.info("Exit Function : sendRequestToUrl");
	   return res;
	   
   }
   

 public  synchronized String HTTP_Request(String host, int port, String path,String method, String data)  {
		    //Resolve the hostname to an IP address
	 StringBuffer response=new StringBuffer();
	 Socket socket =null;
	 try {
		    InetAddress ip = InetAddress.getByName(host);

		    //Open socket to a specific host and port
		    socket = new Socket(host, port);
		        
		    //Get input and output streams for the socket
		    OutputStream out = socket.getOutputStream();
		    InputStream in = socket.getInputStream();

		    // HTTP GET
		    if (method.equals("GET")) {
		      // Constructe a HTTP GET request
		      // The end of HTTP GET request should be \r\n\r\n
		      String request = "GET " + path + "?" + data + " HTTP/1.1\r\n"
		          + "Accept: */*\r\n" + "Host: "+host+"\r\n" 
		          + "Connection: Close\r\n\r\n";
		    
		      // Sends off HTTP GET request
		      out.write(request.getBytes());
		      out.flush();
		    } else if (method.equals("POST")) { // HTTP POST
		      // Constructs a HTTP POST request
		      // The end of HTTP POST header should be \r\n\r\n
		      // After HTTP POST header, it's HTTP POST data
		      // POST it's different from GET: the data of POST is added at the end of HTTP request
	
		      
		      String request = "POST " + path + " HTTP/1.1\r\n" + "Accept: */*\r\n"
				         + "Host: " + host + "\r\n"
				         + "Content-Type: application/x-www-form-urlencoded\r\n"
				         + "Content-Length: " + data.length() + "\r\n\r\n" + data;

		      // Send off HTTP POST request
		      out.write(request.getBytes());
		      out.flush();
		    } else {
		      System.out.println("Invalid HTTP method");
		      socket.close();
		     
		    }
		        
		    // Reads the server's response
		    
		    byte[] buffer = new byte[4096];
		    int bytes_read;

		    // Reads HTTP response
		    while ((bytes_read = in.read(buffer, 0, 4096)) != -1) {
		      // Print server's response 
		      for(int i = 0; i < bytes_read; i++)
		        response.append((char)buffer[i]);
		    }
		        
		    
		    // Closes socket
	 }
	 catch(Exception ex){
		 ex.printStackTrace();
		 return "-100";
	 }
	 finally {
		 
		 try {
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
	 }
		    return response.toString();
		  }
   
//   public synchronized String HTTPpostThroughSocket(String endpoint,String ipaddress,String param,int port)
//   {
//	   fileLogger.info("Inside Function : sendRequestToUrl");
//	   String res = "2";
//	   String outStr;	
//       String statusReq="";
//
//       
//      
//       
//	   try
//	   {
//		 //  String data = URLEncoder.encode("{\"txPower\"", "UTF-8") + "=" + URLEncoder.encode("\"23\"}", "UTF-8");
//		    
//		    Socket socket = new Socket("192.168.0.10", 80);
//
//		    String path =endpoint;
//		    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
//		    wr.write("POST " + path + " HTTP/1.0\r\n");
//		    wr.write("Content-Length: " + param.length() + "\r\n");
//		    wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
//		    wr.write("\r\n");
//
//		    wr.write(param);
//		    wr.flush();
//
//		    BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		    String line;
//		    while ((line = rd.readLine()) != null) {
//		      System.out.println(line);
//		    }
//		    wr.close();
//		    rd.close();
//		    
//		   
//		
//			//   fileLogger.debug("**********Response Received**********");
//	   }catch(Exception e)
//	   {
//		   fileLogger.error("Exception while making request : MSG : "+e.getMessage());
//		   return "-100";
//		   //throw e;
//	   }
//	   fileLogger.info("Exit Function : sendRequestToUrl");
//	   return statusReq;
//	   
//   }
   
   
   
//   public synchronized String HTTPgetThroughSocket(String url,String ipaddress,int port)
//   {
//	   fileLogger.info("Inside Function : sendRequestToUrl");
//	   String res = "2";
//	   String outStr;	
//       String statusReq="";
//
//	   try
//	   {
//		   fileLogger.debug("url is :"+url);
//		   Socket s = new Socket(ipaddress, port);
//           PrintWriter wtr = new PrintWriter(s.getOutputStream());
//           wtr.println("GET "+ url + " HTTP/1.1");
//           wtr.println("Host: "+ipaddress);
//           wtr.println("");
//           wtr.flush();
//           //Creates a BufferedReader that contains the server response
//           BufferedReader bufRead = new BufferedReader(new InputStreamReader(s.getInputStream()));
//          
//           
//
//           //Prints each line of the response 
//           while((outStr = bufRead.readLine()) != null){
//        	   statusReq+=outStr;
//               System.out.println(outStr);
//           }
//
//          // JSONObject jsonobj = new JSONObject(statusReq); 
//			//res=(jsonArray.getJSONObject("data").getString("jammingMode"));
//           bufRead.close();
//           wtr.close();
//           
////           
////           String abc="[{\"freqEndKhz\":\"2168000\",\"freqStartKhz\":\"2112000\",\"profileMode\":\"0\"},{\"freqEndKhz\":\"2168000\",\"freqStartKhz\":\"2112000\",\"profileMode\":\"0\"},{\"freqEndKhz\":\"2168000\",\"freqStartKhz\":\"2112000\",\"profileMode\":\"0\"},{\"freqEndKhz\":\"2168000\",\"freqStartKhz\":\"2112000\",\"profileMode\":\"0\"}]";
////           
////           
////           
////           
////           
////           try {
////               
////    
////               String hostname = "192.168.0.10";
////               int port = 80;
////                
////               //InetAddress addr = InetAddress.getByName(hostname);
////               Socket socket = new Socket(hostname, port);
////               String path = "/jammerConfig.json";
////    
////               // Send headers
////               BufferedWriter wr =
////        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
////               wr.write("POST "+path+" HTTP/1.0rn");
////               wr.write("Content-Length: "+abc.length()+"rn");
////               wr.write("Content-Type: application/x-www-form-urlencodedrn");
////               wr.write("rn");
////    
////               // Send parameters
////               wr.write(abc);
////               wr.flush();
////    
////               // Get response
////               BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
////               String line;
////                
////               while ((line = rd.readLine()) != null) {
////                   System.out.println(line);
////               }
////                
////               wr.close();
////               rd.close();
////                
////           }
////           catch (Exception e) {
////               
////           }
////           
//           
//			   fileLogger.debug("url is :"+url);
//			//   fileLogger.debug("**********Response Received**********");
//	   }catch(Exception e)
//	   {
//		   fileLogger.error("Exception while making request : MSG : "+e.getMessage());
//		   return "-100";
//		   //throw e;
//	   }
//	   fileLogger.info("Exit Function : sendRequestToUrl");
//	   return statusReq;
//	   
//   }
   
   
   
 //method to send http post request
 		public synchronized void sendPostRequestToUrl(String url, JSONObject jo)//LinkedHashMap<String,String> data)
 		{
 			Thread th = new Thread() {public void run() {
 				Response res = null;
 				try
 				{
 					System.out.println("**********sendRequestToUrl**********");
 			        System.out.println("url is :"+url);
 					System.out.println("**********sendRequestToUrl**********");
 			        Client client = ClientBuilder.newClient();
 			        client.property(ClientProperties.CONNECT_TIMEOUT, 10000);
 			        client.property(ClientProperties.READ_TIMEOUT,    10000);
 			        String jsonString = null;
 			        if(jo!=null) {
 			        	jsonString = jo.toString();//new JSONObject(data).toString();
 			        }
 				    WebTarget webTarget = client.target(url);
 				    /*for(String key:queryParam.keySet())
 				    {
 					    webTarget=webTarget.queryParam(key, queryParam.get(key));
 				    }*/ 
 					res = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(jsonString));
 					System.out.println("**********Response Received**********");
 					System.out.println("url is :"+url);
 					System.out.println("**********Response Received**********");
 				}
 				catch(Exception e)
 				{
 		           System.out.println("Exception while making request : MSG : "+e.getMessage());
 		           try {
 		        	   throw e;
 		           } catch (Exception e1) {
 		        	   // TODO Auto-generated catch block
 		        	   e1.printStackTrace();
 		           }
 				}
 			}
 			};
 			
 			th.start();
 			
 			//return res;
 	   	}
   
   public JSONArray getSufiDetail(String id)
   { 
	   fileLogger.info("Inside Function : getSufiDetail");
	  	String query = "select * from view_btsinfo where sytemid = '"+id+"'";
		fileLogger.debug(query);
		JSONArray rs =  new Operations().getJson(query);
		 fileLogger.info("Exit Function : getSufiDetail");
		return rs;
   }
   
   public String getSufiConfigurationWithDefaultValues(String defaultSufiConfig,int type)
   {
	   fileLogger.info("Inside Function : getSufiConfigurationWithDefaultValues");
	   JSONObject jo = null;
	   try
	   {	   
		   jo = new JSONObject(defaultSufiConfig);
		   
		   switch(type)
		   {
		   case 1:
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("SUFI_PARAMS").put("SUFI_OP_MODE", "0");
			   //jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("LAC_POOL_START", "1");
			   //jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("LAC_POOL_END", "1000");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("CELL_ID", "1");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("DL_UARFCN", "10610");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("PRI_SCRAM_CODE", "199");
			   break;
		   case 2:
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("SUFI_PARAMS").put("SUFI_OP_MODE", "1");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("LAC_POOL_START", "1001");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("LAC_POOL_END", "2000");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("CELL_ID", "200");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("DL_UARFCN", "10710");
			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("PRI_SCRAM_CODE", "299");
			   break;
		   case 3:
		   		jo.getJSONObject("SYS_PARAMS").getJSONObject("SUFI_PARAMS").put("SUFI_OP_MODE", "2");
		   		jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("LAC_POOL_START", "2001");
				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("LAC_POOL_END", "3001");
				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("CELL_ID", "220");
				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("DL_UARFCN", "10810");
				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("PRI_SCRAM_CODE", "399");
		   		break;
		   		
		   }
		   
	   }
	   catch(Exception e)
	   {
		   
		   fileLogger.error("getSufiConfigurationWithDefaultValues ERROR : "+e.getMessage());
	   }
	   fileLogger.info("Exit Function : getSufiConfigurationWithDefaultValues");
	   return jo.toString();
   }
   
   public String getSufiConfigurationWithDefaultValues()
   {
	   fileLogger.info("Inside Function : getSufiConfigurationWithDefaultValues");
	   String defaultSufiConfig=null;
	   try
	   {
		   String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		   absolutePath = absolutePath.substring(0,absolutePath.lastIndexOf("WEB-INF"));
		   File file = new File(absolutePath	+ "/resources/config/suficonfig.json");
		   defaultSufiConfig = FileUtils.readFileToString(file);
	   }
	   catch(Exception e)
	   {   
		   fileLogger.error("getSufiConfigurationWithDefaultValues ERROR : "+e.getMessage());
	   }
	   fileLogger.info("Exit Function : getSufiConfigurationWithDefaultValues");
	   return defaultSufiConfig;
   }
   
   public String getNetscanConfigurationWithDefaultValues()
   {
	   fileLogger.info("Inside Function : getNetscanConfigurationWithDefaultValues");
	   String defaultNetscanConfig=null;
	   try
	   {
		   String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		   absolutePath = absolutePath.substring(0,absolutePath.lastIndexOf("WEB-INF"));
		   File file = new File(absolutePath	+ "/resources/config/netscanconfig.json");
		   defaultNetscanConfig = FileUtils.readFileToString(file);
	   }
	   catch(Exception e)
	   {   
		   fileLogger.error("getNetscanConfigurationWithDefaultValues ERROR : "+e.getMessage());
	   }
	   fileLogger.info("Exit Function : getNetscanConfigurationWithDefaultValues");
	   return defaultNetscanConfig;
   }
   
   public String getBandDluarfcnMap()
   {
	   fileLogger.info("Inside Function : getBandDluarfcnMap");
	   String bandDluarfcnMap=null;
	   try
	   {
		   String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		   absolutePath = absolutePath.substring(0,absolutePath.lastIndexOf("WEB-INF"));
		   File file = new File(absolutePath	+ "/resources/config/band_dluarfcn_map.json");
		   bandDluarfcnMap = FileUtils.readFileToString(file);
	   }
	   catch(Exception e)
	   {   
		   fileLogger.error("bandDluarfcnMap ERROR : "+e.getMessage());
	   }
	   fileLogger.info("Exit Function : getBandDluarfcnMap");
	   return bandDluarfcnMap;
   }
   
   public String startTracking(ArrayList<String> givenPLMNs,int antennaId,int trackTime) 
   {
	   fileLogger.info("Inside Function : startTracking");
	   String query = "select opr from view_current_scanned_opr_last_24_hours where opr != 'NA'";
	   JSONArray jo = new Operations().getJson(query);
	   StringBuilder bb = new StringBuilder();
	   
	   try 
	   {
		   for(int i=0;i<jo.length();i++) 
		   {
			   if(i == 0)
			   {
				   bb.append(jo.getJSONObject(i).getString("opr"));
			   }
			   else 
			   {
				   bb.append(","+jo.getJSONObject(i).getString("opr"));
			   }
		   }
	} 
	catch (JSONException e) 
	{
		// TODO Auto-generated catch block
		
		return "{\"result\":\"fail\",\"msg\":\"Problem in geting operators\"}"; 
	}
	   String[] oprs = bb.toString().split(",");
	   
	   
	   
	   if(oprs.length <1 )
	   {
		   return "{\"result\":\"fail\",\"msg\":\"No operators found\"}"; 
	   }
	   PossibleConfigurations pc = new PossibleConfigurations();
	   
	   ArrayList<String> aa = pc.getPlmns(oprs);
	   fileLogger.debug("@vishal packet"+aa.toString());
	   if(givenPLMNs != null) {
		   aa.retainAll(givenPLMNs);
	   fileLogger.debug("@vishal packet"+givenPLMNs.toString());
	   }
	   
	   
	   fileLogger.debug("@vishal packet"+aa.toString());
	   
	   if(!(aa.size()>=1))
	   {
		   new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"No matching PLMN\"}");
		   return "{\"result\":\"fail\",\"msg\":\"No matching PLMN\"}";
	   }
	   
	   
	   pc.antennaId=antennaId;
	   String config = pc.startWithPlmns(aa,antennaId);
	   
	   if(config.equalsIgnoreCase("{\"config_data\":[]}")) 
	   {
		   //new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Unable to genrate Confiuration\"}");
		   return "{\"result\":\"fail\",\"msg\":\"Unable to genrate Confiuration\"}";
	   }
	   
	   String query1 = "update oprrationdata set config ='"+config+"' where id = (select max(id) from oprrationdata)";
	   Common co = new Common();
	   co.executeDLOperation(query1);
	   
	   
	   fileLogger.debug("@vishal packet config "+config);
	   
	   //boolean result = autoTackMobileOnGivenPacketList(config,-1,trackTime);
	   boolean result=true;
	   if(!result) 
	   {
		   return "{\"result\":\"fail\",\"msg\":\"Tracking failed\"}";   
	   }
	   fileLogger.info("Exit Function : startTracking");
	   return "{\"result\":\"success\",\"msg\":\"Tracking finished\"}";
	   
   }
   
   public String startTracking(ArrayList<String> givenPLMNs,String arfcnList,String uarfcnList,String earfcnList,double freq,int antennaId,int trackTime,boolean sysManagerAvailability,boolean switchAvailability) 
   {
	   fileLogger.info("Inside Function : startTracking");
	   Operations operations = new Operations();
	   String config="";
	   boolean result=false;
	   StringBuilder bb = new StringBuilder();
	   try 
	   {
		String antennaProfileName = operations.getJson("select profile_name from antenna where id="+antennaId).getJSONObject(0).getString("profile_name");
		new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Started("+antennaProfileName+")\"}");

		//@deprecated
			new ScanTrackModeServer().sendText("track&Active&"+antennaProfileName);
		/*JSONArray hummerDataArray = new Operations().getJson("select * from view_btsinfo where code = 12");
		if(hummerDataArray.length()>0){
			try {
				new TRGLController().sendTrackingSectorToHummer(antennaProfileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
		}*/
	   
		//new ScanTrackModeServer().sendText("track&Active");
	   
	   
	   fileLogger.debug("givenPLMNs is :"+givenPLMNs);
	   String query = "select opr from view_current_scanned_opr_last_24_hours where opr != 'NA'";
	   JSONArray jo = new Operations().getJson(query);
		   for(int i=0;i<jo.length();i++) 
		   {
			   if(i == 0)
			   {
				   bb.append(jo.getJSONObject(i).getString("opr"));
			   }
			   else 
			   {
				   bb.append(","+jo.getJSONObject(i).getString("opr"));
			   }
		   }
	} 
	catch (JSONException e) 
	{
		// TODO Auto-generated catch block
		
		if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted || e.getMessage().indexOf("interrupted")!=-1){
			DBDataService.isInterrupted = true;
			new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
			fileLogger.error("@interrupt 7 interrupted exception occurs");
		}else{
			return "{\"result\":\"fail\",\"msg\":\"Problem in geting operators\"}";
		} 
	}
	   String[] oprs = bb.toString().split(",");
	   
	   if(oprs.length <1 )
	   {
		   return "{\"result\":\"fail\",\"msg\":\"No operators found\"}"; 
	   }
	   PossibleConfigurations pc = new PossibleConfigurations();
	   
		if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
			DBDataService.isInterrupted = true;
			//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
			DBDataService.isInterrupted = true;
			new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
			fileLogger.debug("@interrupt 8 interrupted exception occurs");
			return "{\"result\":\"fail\",\"msg\":\"Operation Stopped\"}";
		}
	   
	   ArrayList<String> aa = pc.getPlmns(oprs);
	   fileLogger.debug("@vishal packet"+aa.toString());
	   if(givenPLMNs != null) {
		   aa.retainAll(givenPLMNs);
	   fileLogger.debug("@vishal packet"+givenPLMNs.toString());
	   }
	   HashSet<String> hs=new HashSet<String>(aa);
	   fileLogger.debug("@vishal packet"+aa.toString());
	   
	   if(!(aa.size()>=1))
	   {
		   new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"No matching PLMN\"}");
		   return "{\"result\":\"fail\",\"msg\":\"No matching PLMN\"}";
	   }
	   
	   int switchCount=-1;
	   try {
		switchCount=new Operations().getJson("select count(*) count from view_btsinfo where code=8").getJSONObject(0).getInt("count");
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		
		if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
			DBDataService.isInterrupted = true;
			new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
			fileLogger.error("@interrupt 8 interrupted exception occurs");
			return "{\"result\":\"fail\",\"msg\":\"Operation Stopped\"}";
		} 
	}
			   pc.arfcnFilterValue=arfcnList;
			   pc.uarfcnFilterValue=uarfcnList;
			   pc.earfcnFilterValue=earfcnList;
			   pc.antennaId=antennaId;
			   
				if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
					DBDataService.isInterrupted = true;
					//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
					DBDataService.isInterrupted = true;
					new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
					fileLogger.error("@interrupt 8 interrupted exception occurs");
					return "{\"result\":\"fail\",\"msg\":\"Operation Stopped\"}";
				}
				
				
			   config = pc.startWithPlmns(aa,antennaId);
			   
			   if(config.equalsIgnoreCase("{\"config_data\":[]}")) 
			   {
				   new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"No suitable configuration found\"}");
				   return "{\"result\":\"fail\",\"msg\":\"Unable to generate Configuration\"}";
			   }
			   
			   String query1 = "update oprrationdata set config ='"+config+"' where id = (select max(id) from oprrationdata)";
			   Common co = new Common();
			   co.executeDLOperation(query1);
			   
			   
			   fileLogger.debug("@vishal packet config "+config);
				if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
					DBDataService.isInterrupted = true;
					//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
					DBDataService.isInterrupted = true;
					new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
					fileLogger.error("@interrupt 8 interrupted exception occurs");
					return "{\"result\":\"fail\",\"msg\":\"Operation Stopped\"}";
				}
				
			   result = autoTackMobileOnGivenPacketList(config,freq,antennaId,trackTime,sysManagerAvailability,switchAvailability);	
			   fileLogger.debug("out of autoTackMobileOnGivenPacketList");
	   if(!result) 
	   {
		   return "{\"result\":\"fail\",\"msg\":\"Tracking failed\"}";   
	   }
	   fileLogger.info("Exit Function : startTracking"); 
	   return "{\"result\":\"success\",\"msg\":\"Tracking finished\"}";
	   
   }
   
   public String startTracking(ArrayList<String> givenPLMNs,int trackTime) 
   {
	   fileLogger.info("Inside Function : startTracking");
	   String query = "select opr from view_current_scanned_opr_last_24_hours where opr != 'NA'";
	   JSONArray jo = new Operations().getJson(query);
	   StringBuilder bb = new StringBuilder();
	   
	   try 
	   {
		   for(int i=0;i<jo.length();i++) 
		   {
			   if(i == 0)
			   {
				   bb.append(jo.getJSONObject(i).getString("opr"));
			   }
			   else 
			   {
				   bb.append(","+jo.getJSONObject(i).getString("opr"));
			   }
		   }
	} 
	catch (JSONException e) 
	{
		// TODO Auto-generated catch block
		
		return "{\"result\":\"fail\",\"msg\":\"Problem in geting Operators\"}"; 
	}
	   String[] oprs = bb.toString().split(",");
	   
	   
	   
	   if(oprs.length <1 )
	   {
		   return "{\"result\":\"success\",\"msg\":\"No Operators found\"}"; 
	   }
	   
	   
	   ArrayList<String> aa = new PossibleConfigurations().getPlmns(oprs);
	   fileLogger.debug("@vishal packet"+aa.toString());
	   if(givenPLMNs != null)
		   aa.retainAll(givenPLMNs);
	   fileLogger.debug("@vishal packet"+givenPLMNs.toString());
	   
	   
	   
	   fileLogger.debug("@vishal packet"+aa.toString());
	   
	   if(!(aa.size()>=1))
	   {
		   //new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"No matching PLMN\"}");
		   return "{\"result\":\"success\",\"msg\":\"No PLMN found\"}";
	   }
	   
	   //String config = new PossibleConfigurations().startWithPlmns(aa);
	   String config="";
	   if(config.equalsIgnoreCase("{\"config_data\":[]}")) 
	   {
		   //new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Unable to genrate Confiuration\"}");
		   return "{\"result\":\"success\",\"msg\":\"No suitable Configuration\"}";
	   }
	   
	   String query1 = "update oprrationdata set config ='"+config+"' where id = (select max(id) from oprrationdata)";
	   Common co = new Common();
	   co.executeDLOperation(query1);
	   
	   
	   fileLogger.debug("@vishal packet config "+config);
	   
	   //boolean result = autoTackMobileOnGivenPacketList(config,-1,antennaId,trackTime);
	   boolean result=true;
	   if(!result) 
	   {
		   return "{\"result\":\"fail\",\"msg\":\"Tracking Failed\"}";   
	   }
	   fileLogger.info("Exit Function : startTracking");
	   return "{\"result\":\"success\",\"msg\":\"Tracking Finished\"}";   
   }
   
/*   public void switchDsp(String objective){
	   LinkedHashMap<String,String> netscanDspMap= new LinkedHashMap<String,String>();
	   try {
		JSONArray systemManagerArray = new Operations().getJson("select * from view_btsinfo where code=10 limit 1");
		   JSONObject systemManagerObject=systemManagerArray.getJSONObject(0);
			netscanDspMap.put("systemIP", systemManagerObject.getString("ip"));
			if(objective.equals("sufi")){
				netscanDspMap.put("data", "{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"3\",\"DSP1_NODE\":\"1\"}");	
			}else if(objective.equals("scanner")){
				netscanDspMap.put("data", "{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"1\",\"DSP1_NODE\":\"3\"}");
			}else{
				netscanDspMap.put("data", "{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"0\",\"DSP1_NODE\":\"3\"}");	
			}
			netscanDspMap.put("CMD_TYPE", "SET_SYSTEM_CONFIG");
			netscanDspMap.put("SYSTEM_CODE",systemManagerObject.getString("code"));
			netscanDspMap.put("SYSTEM_ID", systemManagerObject.getString("sytemid"));
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		
	}
		new NetscanOperations().sendCommandToNetscanServer(netscanDspMap);
   }*/
   
   
   public int getdspProcessId(String processName) {

	   int processId=99;
	   fileLogger.info("Inside Function : getdspProcessId Restart @getdspProcessId");
	   switch(processName)
	   {
		   case "gsm":  processId=2;
			   			break;
		   
		
		   case "sufi": processId=3; 
			   			break;
		   
		   case "scanner":  processId=1;
			   			break;
		   
		   case "fdd_lte":  processId=4;
			   			break;
		   
		   case "tdd_lte":  processId=5;
			   			break;
		   
		   
		   default: 	processId= 99;
			   			break;
	   }
	   fileLogger.info("Inside Function : getdspProcessId @The processId = "+processId);
	   return processId;
   }
   
	public String switchDsp(String objective, int  dsp0, int dsp1,int l1_attn,String SystemManagerIP) {
		fileLogger.info("Inside Function : switchDsp");
		LinkedHashMap<String, String> netscanDspMap = new LinkedHashMap<String, String>();
		if(DBDataService.isOctasicPowerOn()){
		try {
			JSONArray systemManagerArray = new Operations().getJson("select * from view_btsinfo where code=10 limit 1");
			if(systemManagerArray!=null && systemManagerArray.length()>=1){
			JSONObject systemManagerObject = systemManagerArray.getJSONObject(0);
			String SystemManagerIPToUse="";
			if(SystemManagerIP==null || SystemManagerIP.equalsIgnoreCase(""))
			{
				SystemManagerIPToUse=systemManagerObject.getString("ip");
			}
			else {
				SystemManagerIPToUse=SystemManagerIP;
			}
			netscanDspMap.put("systemIP", SystemManagerIPToUse );
			if (objective.equals("gsm")) {
				netscanDspMap.put("data",
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"2\",\"DSP1_NODE\":\"1\",\"L1_ATT\":\""+l1_attn+"\"}");
			} else if (objective.equals("sufi")) {
				netscanDspMap.put("data",
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"3\",\"DSP1_NODE\":\"1\",\"L1_ATT\":\""+l1_attn+"\"}");
			} else if (objective.equals("scanner")) {
				netscanDspMap.put("data",
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"1\",\"DSP1_NODE\":\"3\",\"L1_ATT\":\""+l1_attn+"\"}");
			}else if (objective.equals("fdd_lte")) {
				netscanDspMap.put("data",
						//"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"4\",\"DSP1_NODE\":\"1\"}");
				
				"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"4\",\"DSP1_NODE\":\"1\",\"L1_ATT\":\""+l1_attn+"\"}");
			}
			else if (objective.equals("tdd_lte")) {
				netscanDspMap.put("data",
						//"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"5\",\"DSP1_NODE\":\"1\"}");
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"5\",\"DSP1_NODE\":\"1\",\"L1_ATT\":\""+l1_attn+"\"}");
			}
			else if (objective.equals("stop_scanner")) {
				netscanDspMap.put("data",
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"0\",\"DSP0_NODE\":\"2\",\"DSP1_NODE\":\"1\"}");
			}
			else if (objective.equals("start_scanner")) {
				netscanDspMap.put("data",
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"0\",\"DSP1_NODE\":\"1\",\"L1_ATT\":\""+l1_attn+"\"}");
			}
			else if (objective.equals("shutdown_octasic")) {
				netscanDspMap.put("data",
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"2\",\"DSP0_NODE\":\"0\",\"DSP1_NODE\":\"1\"}");
			}
			//restart_process below
			else if (objective.equals("restart")) {
				fileLogger.info("Inside Function : switchDsp Restart @Vaibhav");
				netscanDspMap.put("data",
					"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"3\",\"DSP0_NODE\":"+dsp0+",\"DSP1_NODE\":"+dsp1+"}");
			}
			else {
				netscanDspMap.put("data",
						"{\"CMD_CODE\":\"SET_SYSTEM_CONFIG\",\"OPERATION\":\"1\",\"DSP0_NODE\":\"0\",\"DSP1_NODE\":\"3\",\"L1_ATT\":\""+l1_attn+"\"}");
			}
			netscanDspMap.put("CMD_TYPE", "SET_SYSTEM_CONFIG");
			netscanDspMap.put("SYSTEM_CODE", systemManagerObject.getString("code"));
			netscanDspMap.put("SYSTEM_ID", systemManagerObject.getString("sytemid"));
			return new NetscanOperations().sendCommandToNetscanServer(netscanDspMap);
			}else{
				return "system manager not present";
			}
		} catch (JSONException e) {
			fileLogger.error("exception in switchDsp mesage :"+e.getMessage());
			fileLogger.info("Exit Function : switchDsp");
			return "";
			// TODO Auto-generated catch block
			
		}
		}else{
			fileLogger.info("Exit Function : switchDsp");
			return "";
		}
			
	}
   
 /*  public void switchAntenna(int antennaId){
	   
   }*/
   
   public boolean autoTackMobileOnGivenPacketList(String configData,double freq,int antennaId,int trackTime,boolean sysManagerAvailability,boolean switchAvailability)
   {
	   fileLogger.info("Inside Function : autoTackMobileOnGivenPacketList");
	   int TrackingcontinueuponFailure=Integer.parseInt(DBDataService.configParamMap.get("TrackingcontinueuponFailure"));
	   boolean error_exists_tracking= false; 
	 //  String tracktimeUpdate="select value from system_properties where key='hold_time';";
	  
	   int holdTime= Integer.parseInt(DBDataService.configParamMap.get("HoldTime"));
	   int holdTimeint=holdTime;	
	   if(holdTime!=0)
	   {
		   
		    holdTimeint+=Integer.parseInt(DBDataService.configParamMap.get("addition_time"));
	   }
//	   try {
//		
//
//			   JSONArray js = new Operations().getJson(tracktimeUpdate);
//			
//			   holdTimeint=js.getJSONObject(0).getInt("value");
//			   fileLogger.error("Inside autoTackMobileOnGivenPacketList 1 - holdTimeint="+holdTimeint);
//		   
//	   } catch (JSONException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	   }
//		
//	//   if(holdTimeint>0) {
//		   
//		   int additional_time=Integer.parseInt(DBDataService.configParamMap.get("addition_time"));
//	 //  }
	   fileLogger.info("autoTackMobileOnGivenPacketList  holdTimeint =  "+holdTimeint);
	   fileLogger.info("autoTackMobileOnGivenPacketList  trackTime =  "+trackTime);
	   
	   
	   
	   int attn_prev=-1;
	   String tech_prev="";
	   boolean error_flag=false;
	   String prev_ip="";
	   boolean running_2g=false;
	   boolean running_3g=false;
	   boolean running_4g=false;
	   
	   fileLogger.info("autoTackMobileOnGivenPacketList  TrackingcontinueuponFailure =  "+TrackingcontinueuponFailure);
	   System.out.println(DBDataService.isInterrupted);
	   fileLogger.debug("in autoTackMobileOnGivenPacketList");
	   HashMap<String,Integer> powerSettingValue= new HashMap<String,Integer>();
	   long sysManWait=4000l;
		try{
			sysManWait=Long.parseLong(DBDataService.configParamMap.get("sysmanwait"));
			fileLogger.debug("@sysManWait is :"+sysManWait);
		}catch(Exception e){
			fileLogger.error("exception in getting sysManWait message :"+sysManWait);
		}
	   //HashMap<String,String> powerSettingMap= new Common().getPowerSetting1();
	   String falconType=DBDataService.configParamMap.get("falcontype");
	   fileLogger.debug("@track falconType is :"+falconType);
	   
	   AuditHandler auditHandler = new AuditHandler();
	   Operations operations = new Operations();

	   int sectorId = new CommonService().getSectorFromAntennaId(antennaId);
	   updateActiveTrackingAntenna(antennaId);
	   //switchAntenna(antennaId); 
	   JSONObject configJson = null;
	   
	   JSONArray json2gArray=new JSONArray();
	   
	   JSONArray json3gArray=new JSONArray();
	   JSONArray json4gArray=new JSONArray();
	   
	   HashMap<String,ArrayList<HashMap<String,String>>> devicesMapOverTech = operations.getAllBtsInfoByTech();
	   String auditArfcn="";
	   String auditUarfcn="";
	   String auditPsc="";
	   String auditEarfcn="";
	   String auditPci="";
	   String lteCellId="1";
	   
	   try{
		   String antennaProfileName = operations.getJson("select profile_name from antenna where id="+antennaId).getJSONObject(0).getString("profile_name");
		   //long configHoldTime=operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value");
			if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
				DBDataService.isInterrupted = true;
				//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
                return false;
			}
		   //final int repititionFreq = configJson.getInt("repitition_freq");
			configJson = new JSONObject(configData);
		   JSONArray dataArray = configJson.getJSONArray("config_data");
		   
		   for(int i=0;i<dataArray.length();i++)
		   {
			   String tech=dataArray.getJSONObject(i).getString("tech");
			
			if(tech.equalsIgnoreCase("2g")){
				   json2gArray.put(dataArray.getJSONObject(i)); 
			   }else if(tech.equalsIgnoreCase("3g")){
				   json3gArray.put(dataArray.getJSONObject(i));
			   }else if(tech.equalsIgnoreCase("4g")){
				   json4gArray.put(dataArray.getJSONObject(i));
			   }else{
				   JSONArray tempJsonArr=dataArray.getJSONObject(i).getJSONArray("data");
				   for(int j=0;j<tempJsonArr.length();j++){
					 JSONObject tempJsonObj=tempJsonArr.getJSONObject(j); 
					 if(tempJsonObj.getString("flag").equalsIgnoreCase("self")){
						 if(!tempJsonObj.getString("arfcn").equalsIgnoreCase("")){
							 json2gArray.put(dataArray.getJSONObject(i));
							 fileLogger.debug("line 678 :"+json2gArray.length());
						 }else if(!tempJsonObj.getString("uarfcn").equalsIgnoreCase("")){
							 json3gArray.put(dataArray.getJSONObject(i));
							 fileLogger.debug("line 681 :"+json3gArray.length());
						 }else if(!tempJsonObj.getString("earfcn").equalsIgnoreCase("")){
							 json4gArray.put(dataArray.getJSONObject(i));
							 fileLogger.debug("line 684 :"+json4gArray.length());
						 }
					 }
				   }
			   }
		   }
		   
		   fileLogger.debug("2g array size :"+json2gArray.length());
		   fileLogger.debug("3g array size :"+json3gArray.length());
		   fileLogger.debug("4g array size :"+json4gArray.length());

		   ArrayList<JSONObject> json2gList=CommonService.getSortedList(json2gArray);
		   
		   ArrayList<JSONObject> json3gList=CommonService.getSortedList(json3gArray);
		   ArrayList<JSONObject> json4gList=CommonService.getSortedList(json4gArray);
	       
		   int json2gListSize=json2gList.size();
	       int json3gListSize=json3gList.size();
	       int json4gListSize=json4gList.size(); 
	       
		   int largeListSize=json2gListSize>=json3gListSize?(json2gListSize>=json4gListSize?json2gListSize:json4gListSize):(json4gListSize>=json3gListSize?json4gListSize:json3gListSize);
	       
		   ArrayList<JSONObject> newJson2gList=new ArrayList<JSONObject>();
	       ArrayList<JSONObject> newJson3gList=new ArrayList<JSONObject>();
	       ArrayList<JSONObject> newJson4gList=new ArrayList<JSONObject>();
	       
			if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
				DBDataService.isInterrupted = true;
				//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
                return false;
			}
			
			
			/* **********************start**************************** */
			LinkedHashMap<String,PacketCount> packetMap = new LinkedHashMap<String,PacketCount>();
			  for(int i=0;i<json2gList.size();i++){
				  if(packetMap.get(json2gList.get(i).getString("plmn"))!=null){
					  PacketCount dt=packetMap.get(json2gList.get(i).getString("plmn"));
					  int count=dt.getCount2g();
					  if(count==0){
						  dt.setInitIndex2g(i);
					  }
					  count++;
					  dt.setCount2g(count);
				  }else{
					  PacketCount dt=new PacketCount(1,i,0,-1,0,-1);
					  packetMap.put(json2gList.get(i).getString("plmn"), dt);
				  }
			  }
			  for(int j=0;j<json3gList.size();j++){
				  if(packetMap.get(json3gList.get(j).getString("plmn"))!=null){
					  PacketCount dt=packetMap.get(json3gList.get(j).getString("plmn"));
					  int count=dt.getCount3g();
					  if(count==0){
						  dt.setInitIndex3g(j);
					  }
					  count++;
					  dt.setCount3g(count);
				  }else{
					  PacketCount dt=new PacketCount(0,-1,1,j,0,-1);
					  packetMap.put(json3gList.get(j).getString("plmn"), dt);
				  }
			  }
			  for(int k=0;k<json4gList.size();k++){
				  if(packetMap.get(json4gList.get(k).getString("plmn"))!=null){
					  PacketCount dt=packetMap.get(json4gList.get(k).getString("plmn"));
					  int count=dt.getCount4g();
					  if(count==0){
						  dt.setInitIndex4g(k);
					  }
					  count++;
					  dt.setCount4g(count);
				  }else{
					  PacketCount dt=new PacketCount(0,-1,0,-1,1,k);
					  packetMap.put(json4gList.get(k).getString("plmn"), dt);
				  }
			  }
			  
			   for(String hset:packetMap.keySet()){
				   PacketCount dt=packetMap.get(hset);
				   int count2g=dt.getCount2g();
				   int count3g=dt.getCount3g();
				   int count4g=dt.getCount4g();
				   
				   if(count2g>count3g){
					   if(count3g>count4g){
						   if(count2g>count4g){
							   //case 1
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count4g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count3g;j++){
								   newJson2gList.add(json2gList.get(i2g));
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson4gList.add(jobj);
								   i2g++;
								   i3g++;
							   }
							   for(int k=j;k<count2g;k++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj2.put("duration",json2gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   i2g++;
							   }
						   }else{
							   //case 2
							   //not applicable
						   }
					   }else{
						   if(count2g>count4g){
							   //case 3
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count3g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count4g;j++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
								   newJson4gList.add(json4gList.get(i4g));
								   i2g++;
								   i4g++;
							   }
							   for(int k=j;k<count2g;k++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj2.put("duration",json2gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   i2g++;
							   }
						   }else{
							   //case 4
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count3g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count2g;j++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
								   newJson4gList.add(json4gList.get(i4g));
								   i2g++;
								   i4g++;
							   }
							   for(int k=j;k<count4g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj2.put("duration",json4gList.get(i).getString("duration"));
								   newJson3gList.add(jobj2);
								   newJson4gList.add(json4gList.get(i4g));
								   i4g++;
							   }
						   } 
					   }
				   }else{
					   if(count3g>count4g){
						   if(count2g>count4g){
							   //case 5
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count4g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count2g;j++){
								   newJson2gList.add(json2gList.get(i2g));
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson4gList.add(jobj);
								   i2g++;
								   i3g++;
							   }
							   for(int k=j;k<count3g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj2.put("duration",json3gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   i3g++;
							   }
						   }else{
							   //case 6
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count2g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count4g;j++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
								   newJson4gList.add(json4gList.get(i4g));
								   i3g++;
								   i4g++;
							   }
							   for(int k=j;k<count3g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj2.put("duration",json3gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   i3g++;
							   }
						   }
					   }else{
						   if(count2g>count4g){
							   //case 7
							   //not applicable
						   }else{
							   //case 8
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count2g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count3g;j++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
								   newJson4gList.add(json4gList.get(i4g));
								   i3g++;
								   i4g++;
							   }
							   for(int k=j;k<count4g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj2.put("duration",json4gList.get(i).getString("duration"));
								   newJson3gList.add(jobj2);
								   newJson4gList.add(json4gList.get(i4g));
								   i4g++;
							   }
						   } 
					   }
				   }
			   }
			  
			  
			/* **********************end**************************** */
		       
	       JSONArray  newJson2gArray = new JSONArray();
	       JSONArray  newJson3gArray = new JSONArray();
	       JSONArray  newJson4gArray = new JSONArray();
	       for(int i=0;i<newJson2gList.size();i++){
	    	   newJson2gArray.put(newJson2gList.get(i));
	    	   newJson3gArray.put(newJson3gList.get(i));
	    	   newJson4gArray.put(newJson4gList.get(i));
	       }
		   
		   JSONArray JSONArrayFor2g=newJson2gArray;
		   JSONArray JSONArrayFor3g=newJson3gArray;
		   JSONArray JSONArrayFor4g=newJson4gArray;
		   

		   //setDbCounterValue("update config_status set start_time=now(),counter=0");
			//JSONArray jarr=getDbCounterValue("select start_time from config_status");
			//oprStartTime=jarr.getJSONObject(0).getString("start_time");
			
			int l1 = newJson2gArray.length();
			int l2 = newJson3gArray.length();
			int l3 = newJson4gArray.length();
			
			int countl1l2 = l1>l2?(l1>l3?l1:l3):(l3>l2?l3:l2);
			
			if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
				DBDataService.isInterrupted = true;
				//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
                return false;
			}
			
			new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configuration Packet : GSM "+json2gListSize+" : UMTS "+json3gListSize+" : LTE "+json4gListSize+" \"}");
			fileLogger.debug("Configuration Packet : GSM "+json2gListSize+" : UMTS "+json3gListSize+" : LTE "+json4gListSize);
			int iterationCount = 0;
/*			Date currDate = new Date();
		    Calendar calendar = Calendar. getInstance();
		    calendar.setTime(currDate);
		    calendar.add(Calendar.MINUTE,trackTime);
		    long futureTime=calendar.getTimeInMillis();*/
				//String query = "select round(extract(epoch from t_stoptime)*1000) as stoptime from oprrationdata order by id desc limit 1";
				//fileLogger.debug(query);
				//JSONArray rs =  new Operations().getJson(query);
				
				/*if(iterationCount != 0) 
				{
					//fileLogger.debug("curr and stop time:"+System.currentTimeMillis()+","+rs.getJSONObject(0).getLong("stoptime"));
					//fileLogger.debug("current time: "+System.currentTimeMillis()+" @vishal: "+rs.getJSONObject(0).getLong("stoptime"));
					fileLogger.debug("in while of autotrack");
					fileLogger.debug("futureTime is :"+futureTime+" and trackTime is :"+trackTime+"and current time is "+System.currentTimeMillis());
					if ( trackTime!=-1 && System.currentTimeMillis()>=futureTime) 
					{
						//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Done\"}");
						break;
					}
				}*/
				new CurrentOperation(Thread.currentThread());
				fileLogger.debug("@vishal1"+countl1l2);
				
				OperationCalculations oc = new OperationCalculations();
				
				
				
				//int distance = new Operations().getJson("select * from oprrationdata where id = (select max(id) from oprrationdata)").getJSONObject(0).getInt("distance");
				int sleepTimeAfter4gOver=0;
				int distance=DBDataService.getInstance().getCurrentEventData().getCoverageDistance();
				for(int i=0;i<countl1l2;i++)
				{
				   running_2g=false;
				   running_3g=false;
				   running_4g=false;
					   
					
					//String query1 = "select round(extract(epoch from t_stoptime)*1000) as stoptime from oprrationdata order by id desc limit 1";
					//fileLogger.debug(query1);
					//JSONArray rs1 =  new Operations().getJson(query1);
					fileLogger.debug("in while of autotrack");
/*					fileLogger.debug("futureTime is :"+futureTime+" and trackTime is :"+trackTime+"and current time is "+System.currentTimeMillis());
					if (trackTime!=-1 && System.currentTimeMillis()>=futureTime) 
					{
						
						//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Done\"}");
						break;
					}*/
					
					if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
						DBDataService.isInterrupted = true;
						//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
                        return false;
					}else{
						if(falconType.equalsIgnoreCase("standard")){
						HashMap<String,String> statusMap=lockUnlockAllDevicesOnAutoTrack(devicesMapOverTech,1);
						if(statusMap.get("status").equals("failure")){
							new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+statusMap.get("ip")+"\"}");
							//new CommonService().updateStatusOfGivenBts("all");
							//new DeviceStatusServer().sendText("ok");
							if(TrackingcontinueuponFailure==1) {
									
								    continue;
							}
							else
								return false;
						
						}else{
							//new DeviceStatusServer().sendText("all");
							updateDeviceStatus("lock","all");
							new DeviceStatusServer().sendText("ok");
						}
						}else{
						//lockUnlockAllDevices(devicesMapOverTech, 1);
							
						//not req in Octasic system
						}
					}
					fileLogger.debug("@vishal2"+countl1l2);
					boolean gone_in_2g_3g_4g=false;
					try
					{
						
						HashMap device = new HashMap();
						if(JSONArrayFor2g.length()>0 && devicesMapOverTech.get("2G").size()>0)
						{
								
							HashMap<String,String> hm=null;
							ArrayList<HashMap<String, String>> ArrayFor2gdevice=devicesMapOverTech.get("2G");
							
							 for(int k=0;k<ArrayFor2gdevice.size();k++) {
							 
								 for(int n=0;n<JSONArrayFor2g.length();n++) {
									 JSONObject tempJsonObjectFor2g=new JSONObject();
									 try {
										 
										 tempJsonObjectFor2g=JSONArrayFor2g.getJSONObject(n);
									 }
									 catch(Exception ex)
									 {
										 ex.printStackTrace();
									 }
									 sleepTimeAfter4gOver=(tempJsonObjectFor2g.getInt("duration")*60*1000);
									 if(device.containsKey("2G-"+n)) {
										 
										 continue;
									 }
									 if(!tempJsonObjectFor2g.has("dummyCheck")) {
										 gone_in_2g_3g_4g =true;
										 JSONArray data= new JSONArray();
										 String Band2g="";
										 try {
											 
											 data = (JSONArrayFor2g.getJSONObject(n).getJSONArray("data")); 
											 Band2g=(data.getJSONObject(0).getString("band"));
										 }
										 catch(Exception ex)
										 {
											 ex.printStackTrace();
										 }
										 String devicebnd=ArrayFor2gdevice.get(k).get("hw");
										// devicebnd=devicebnd.substring(devicebnd.lastIndexOf("-") + 1);
										 String band2g_=getSupportedBand("2G",Band2g);
										 if (!checkSupportedDevice(devicebnd,band2g_))
										 	continue;
										 
										 device.put("2G-"+n, 1);
										 hm=devicesMapOverTech.get("2G").get(k);
										 
									    running_2g=true;
								  
									//HashMap<String,String> hm3g=devicesMapOverTech.get("3G").get(0);
									//JSONObject tempJsonObjectFor2g=JSONArrayFor2g.getJSONObject(i);
								    
									
									
									try
									{	
										fileLogger.debug("@vishal2g:"+tempJsonObjectFor2g.toString());
										
										if(!tempJsonObjectFor2g.has("dummyCheck"))
										{
											//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Applying Configuration for GSM\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
											
											double caulatedFreq = -1;
											if(freq == -1) 
											{
												JSONArray packetArrayFor2g = tempJsonObjectFor2g.getJSONArray("data");
												
												int length = packetArrayFor2g.length();
												for(int j=0;j<length;j++)
												{
													
													JSONObject tempObj = packetArrayFor2g.getJSONObject(j);
													
													if(tempObj.getString("flag").equalsIgnoreCase("self")) 
													{
														int arfcn = tempObj.getInt("arfcn");
														
														fileLogger.debug("@dani***************getting frequecny*******************");
														caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(arfcn,"2G");
														fileLogger.debug(arfcn);
														//Thread.sleep(60000);
														fileLogger.debug("@dani***************getting frequecny*******************");
														
														
													}
												}
											}
											else 
											{
												caulatedFreq = freq;
											}
											
											String bandrr = "P_900";
											/*if(packBand.equalsIgnoreCase("4"))
											{
												bandrr="DCS_1800";
											}*/
											JSONArray compPacketArr=tempJsonObjectFor2g.getJSONArray("data");
											String packArfcn="";
											String packBand="";
											for(int packSize=0;packSize<compPacketArr.length();packSize++){
												JSONObject packObj=compPacketArr.getJSONObject(packSize);
												if(packObj.getString("flag").equals("self")){
													packArfcn=packObj.getString("arfcn");
													packBand=packObj.getString("band");
													break;
												}
											}
											
											String band2g="";
//											switch(packBand){
//											case "1":
//												band2g="850";
//												break;
//											case "2":
//												band2g="900";
//												break;
//											case "4":
//												band2g="1800";
//												break;
//											}
											band2g=getSupportedBand("2G",packBand);
											
											fileLogger.debug("@vaibhav band2g="+band2g);
											
											///JSONArray currentDeviceArray = new Operations().getJson("select * from btsmaster where ip = '" + hm.get("ip") + "' ;");
											//SystemManagerToSwitchDSP=currentDeviceArray.getJSONObject(0).getString("systemmanager");
											
											
											
											
											
											
											String SystemManagerToSwitchDSP=hm.get("systemmanager");
											if(SystemManagerToSwitchDSP!=null)
											{
												if (SystemManagerToSwitchDSP.equals("")==false ) {
													// if(falconType.equalsIgnoreCase("octasic")){
													
													String respData="";
													if (SystemManagerToSwitchDSP.equals("")==false && SystemManagerToSwitchDSP!=null)  {
														if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag) {
															error_flag=false;
															respData=switchDsp("restart",2,99,999,SystemManagerToSwitchDSP);
														}
														else{
															respData=switchDsp("gsm",0,0,999,SystemManagerToSwitchDSP);
														}
														
														
														if(respData.equals("")){
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:GSM\"}");
															
															if(TrackingcontinueuponFailure==1) {
																break;
															}
															else
																return false;
														}else{
															fileLogger.debug("@sleep about to sleep in GSM System Manager Switching");
															Thread.sleep(sysManWait);
														}
													}
												}
											}

											int powerGSM=-999;
											if(falconType.equalsIgnoreCase("standard")){
												powerGSM = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
											}else{
												//powerGSM=getPowerFromPowerSetting("2G",band2g,caulatedFreq)antennaId.get("sdf");
												powerSettingValue=getPowerFromPowerSetting("2G",band2g,caulatedFreq);
												powerGSM=powerSettingValue.get("power_setting");
												
												//powerGSM = Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-2G-"+band2g +"-1"));
											}
											DBDataService.configurdTxPower=powerGSM;
										    DBDataService.configuredFreq=caulatedFreq;
											//send antina over udp
											
											/*if(packBand.equalsIgnoreCase("4"))
											{
												bandrr="DCS_1800";
											}*/
											auditArfcn=packArfcn;
											
											String result="";
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
												//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
												return false;
											}else{
												result= operations.locateWithNeighbour(tempJsonObjectFor2g,hm.get("ip"),powerGSM);	
											}
											if(result == null || result.equalsIgnoreCase("{\"STATUS\":\"3\"}")) 
											{
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:GSM(ARFCN:"+packArfcn+")\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;
													prev_ip= hm.get("ip");
													break;
												}
												else
													return false;
												
											}else{
												new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:GSM(ARFCN:"+packArfcn+")\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
											}
											fileLogger.debug("@vishal2g1");
											operations.createNeighbourServerData(tempJsonObjectFor2g,hm.get("ip"),powerGSM);
											//boolean isSucceded = switchAntena(tempJsonObjectFor2g.getJSONArray("data").getJSONObject(0).getString("band"),"2g","8");
											boolean isSucceded = true;
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
												//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
						                        return false;
											}else{
													//if(falconType.equalsIgnoreCase("standard")){
													if(SystemManagerToSwitchDSP!=null) {
														if (SystemManagerToSwitchDSP.equals("")==false ) {
														
															isSucceded = switchAntena(band2g,"2G","8",sectorId);
														}else{
															isSucceded = switchAntena(band2g,"3G","8",sectorId);
														}
													}
												}

											if(!isSucceded) 
											{
												if (DBDataService.rotatedMode==2)
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:GSM\"}");
													else
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:GSM\"}");
												if(TrackingcontinueuponFailure==1) {
													break;
												}
												else
													return false;
												
											}
											else
											{
												isSucceded = true;
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
													DBDataService.isInterrupted = true;
													//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
							                        return false;
												}else{
														//if(falconType.equalsIgnoreCase("standard")){
													if(SystemManagerToSwitchDSP!=null) {
															if ((SystemManagerToSwitchDSP.equals("")==false )) {
																isSucceded =switchAntena(band2g+"-NA","2G","7",sectorId);
															}else{
																isSucceded = switchAntena(band2g+"-NA","3G","7",sectorId);	
															}	
														}
													}
												
												
												if(!isSucceded){
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:UMTS\"}");
													if(TrackingcontinueuponFailure==1) {
														break;
													}
													else
														return false;
													
												}
											}
											fileLogger.debug("@t to Check the StatusMAnual Tracking");
											String statusResponse="";
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
												//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
						                        return false;
											}else{
												statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"2");	
											}
											LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
											log.put("IP",hm.get("ip"));
											log.put("Action","UNLOCK");
											log.put("ARFCN",auditArfcn);
											log.put("TECH",hm.get("typename"));
											log.put("Sector",antennaProfileName);
											
											
											if(statusResponse==null || statusResponse.equals("")){
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
												new DeviceStatusServer().sendText("ok");
												log.put("Error","Connection Error on Unlocking");
												auditHandler.auditConfigExt(log);
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;
													prev_ip= hm.get("ip");
													break;
												}
												else
													return false;
												
											}else{
												//new DeviceStatusServer().sendText(hm.get("ip"));
												updateDeviceStatus("unlock",hm.get("ip"));
												new DeviceStatusServer().sendText("ok");
												auditHandler.auditConfigExt(log);
											}
											
											fileLogger.debug("@vishal2g3");
										}
										else
										{
											fileLogger.debug("dummy packet creation in 2G for synchronization with other technologies");
										}
									}
									catch(Exception E)
									{
										fileLogger.debug("@vishal exception in 2g packet thread :"+E.getMessage());
										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
											DBDataService.isInterrupted = true;
											new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
											fileLogger.error("@interrupt 9 interrupted exception occurs");
											return false;
										}
										if(TrackingcontinueuponFailure==1) {
											break;
										}
										else
											return false;
										
									}
								        
									if(!tempJsonObjectFor2g.has("dummyCheck")) 
									{
										break;
										
										
										
//										fileLogger.debug("@vishal2g4");
//										fileLogger.debug("about to sleep in gsm configuration");
//									    //Thread.sleep(tempJsonObjectFor2g.getInt("duration")*60*1000);
//										fileLogger.debug("@sleep in 2g cue");
//										if(trackTime==-1){
//											
//											Thread.sleep((operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value")+holdTimeint)*1000);
//											fileLogger.debug("@Hey NEW TIME HOLD 5 2g  :"+(operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value")+holdTimeint)*1000);
//										}else{
//											fileLogger.debug("@Hey NEW TIME HOLD 6 2g :"+((trackTime*60)+holdTimeint)*1000);
//											Thread.sleep(((trackTime*60)+holdTimeint)*1000);
//											
//										}
//										String statusResponse="";
//										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
//											DBDataService.isInterrupted = true;
//											//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
//					                        return false;
//										}else{
//											statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"1");
//											
//										}
//										LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
//										log.put("IP",hm.get("ip"));
//										log.put("Action","LOCK");
//										log.put("ARFCN",auditArfcn);
//										
//										if(statusResponse==null || statusResponse.equals("")){
//											new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
//											new DeviceStatusServer().sendText("ok");
//											log.put("ERROR","Connection Error on Locking ");
//											auditHandler.auditConfigExt(log);
//											if(TrackingcontinueuponFailure==1) {
//												error_flag=true;
//												prev_ip= hm.get("ip");
//												continue;
//											}
//											else
//												return false;
//											//retrun false;;
//										}else{
//											//new DeviceStatusServer().sendText(hm.get("ip"));
//											updateDeviceStatus("lock",hm.get("ip"));
//											new DeviceStatusServer().sendText("ok");
//											auditHandler.auditConfigExt(log);
//											
//										}
									}
								 }
							 }
						 }
					}

					fileLogger.debug("@vishal2.5"+JSONArrayFor3g.length()+""+devicesMapOverTech.get("3G").size());
					
						if(JSONArrayFor3g.length()>0 && devicesMapOverTech.get("3G").size()>0)
						{
							ArrayList<HashMap<String, String>> ArrayFor3gdevice=devicesMapOverTech.get("3G");
							for(int k=0;k<ArrayFor3gdevice.size();k++) {
								 for(int n=0;n<JSONArrayFor3g.length();n++) {
										 if(device.containsKey("3G-"+n))
										 {
											 continue;
										 }
										 JSONObject tempJsonObjectFor3g=JSONArrayFor3g.getJSONObject(n);
								
									    running_3g=true;
									  
			
										ThreegOperations threegOperations=new ThreegOperations();
										fileLogger.debug("@vishal3");
										HashMap<String,String> hm=devicesMapOverTech.get("3G").get(k);
										//HashMap<String,String> hm1=devicesMapOverTech.get("2G").get(0);
										
										LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
										fileLogger.debug("@vishal4");
									    data.put("cmdType", "SET_CELL_UNLOCK");
									    data.put("systemCode", hm.get("dcode"));
									    data.put("systemId", hm.get("sytemid"));
									    data.put("systemIP", hm.get("ip"));
									    data.put("data", "{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
									
									    fileLogger.debug("@vishal5");
									    fileLogger.debug("line 879:"+tempJsonObjectFor3g.toString());
									    fileLogger.debug("@vishal6");
									    try{
											fileLogger.debug("@vishal3g:"+tempJsonObjectFor3g.toString());
												if(!tempJsonObjectFor3g.has("dummyCheck"))
												{
													
													device.put("3G-"+n, 1);
													gone_in_2g_3g_4g =true;
													
													//String SystemManagerToSwitchDSP="";
												//	JSONArray currentDeviceArray = new Operations().getJson("select * from btsmaster where ip = '" + hm.get("ip") + "' ;");
												//	SystemManagerToSwitchDSP=currentDeviceArray.getJSONObject(0).getString("systemmanager");
													
													String SystemManagerToSwitchDSP=hm.get("systemmanager");
													if(SystemManagerToSwitchDSP!=null){
															if (SystemManagerToSwitchDSP.equals("")==false  ) {
														
																String respData="";
														
																
																if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag && !running_2g ) {
																	error_flag=false;
																	respData= switchDsp("restart",3,99,99,SystemManagerToSwitchDSP);
																	}
																else{
																	respData= switchDsp("sufi",0,0,999,SystemManagerToSwitchDSP);
																}
																
																if(respData.equals("")){
																
																
																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:UMTS\"}");
																if(TrackingcontinueuponFailure==1) {
																	break;
																}
																else
																	return false;

																}else{
																fileLogger.debug("@sleep about to sleep in UMTS System Manager Switching");
																
																Thread.sleep(sysManWait);
																}
															}	
														}
													
													
													JSONArray compPacketArr=tempJsonObjectFor3g.getJSONArray("data");
													String packUarfcn="";
													String packPsc="";
													String packBand="";
												for(int packSize=0;packSize<compPacketArr.length();packSize++){
													JSONObject packObj=compPacketArr.getJSONObject(packSize);
													if(packObj.getString("flag").equals("self")){
														packUarfcn=packObj.getString("uarfcn");
														packPsc=packObj.getString("psc");
														packBand=packObj.getString("band");
														break;
													}
												}
												auditUarfcn=packUarfcn;
												auditPsc=packPsc;
													fileLogger.debug("@vishal7");
														double caulatedFreq = 1;
													
													if(freq == -1) 
													{
														JSONArray cellConfigurationArr=tempJsonObjectFor3g.getJSONArray("data");
														
														
														for(int kk=0;kk<cellConfigurationArr.length();kk++){
															JSONObject tempJsonObjectForCell=cellConfigurationArr.getJSONObject(kk);
															if(tempJsonObjectForCell.getString("flag").equalsIgnoreCase("self")){
																
																String newDluarfcn=tempJsonObjectForCell.getString("uarfcn");
																String  newUluarfcn = null;
																int dlUarfcnToUluarfcnFormula=new PossibleConfigurations().getFormulaForTheGivenUarfcn(Integer.parseInt(newDluarfcn));
																if(dlUarfcnToUluarfcnFormula!=0)
																{
																	newUluarfcn=Integer.toString(Integer.parseInt(newDluarfcn)+(dlUarfcnToUluarfcnFormula));
																	caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(Integer.parseInt(newDluarfcn),"3G");
																}	
															}
														}
													}
													
													String band3g="";
	//												switch(packBand){
	//												case "5":
	//													band3g="850";
	//													break;
	//												case "8":
	//													band3g="900";
	//													break;
	//												case "3":
	//													band3g="1800";
	//													break;
	//												case "1":
	//													band3g="2100";
	//												}
													band3g= getSupportedBand("3G",packBand);
													fileLogger.debug("@vaibhav band3g="+band3g);
													distance=10;
													//int powerUMTS = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
													//int powerUMTS=Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-3G-"+band3g +"-1"));
													
													//int powerUMTS=getPowerFromPowerSetting("3G",band3g,caulatedFreq);
													powerSettingValue=getPowerFromPowerSetting("3G",band3g,caulatedFreq);
													int powerUMTS=-999;
													powerUMTS=powerSettingValue.get("power_setting");
													DBDataService.configurdTxPower=powerUMTS;
												    DBDataService.configuredFreq=caulatedFreq;
													
													LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"),powerUMTS);
													//LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"));
													//LinkedHashMap<String,String> param=null;
													boolean result=false;
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
														// AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
								                        return false;
													}else{
														result = new Common().sendConfigurationToNode(Integer.parseInt(param.get("adminState")),param);
														fileLogger.debug("@vishal8");	
													}
													
													if(!result) 
													{
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:UMTS(UARFCN:"+packUarfcn+",PSC:"+packPsc+")\",\"packet\":"+tempJsonObjectFor3g.toString()+"}");
														fileLogger.debug("@vishal9");
														if(TrackingcontinueuponFailure==1) {
															error_flag=true;
															prev_ip= hm.get("ip");
															break;
														}
														else
															return false;

													}else{
														new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:UMTS(UARFCN:"+packUarfcn+",PSC:"+packPsc+")\",\"packet\":"+tempJsonObjectFor3g.toString()+"}");
													}
													param.put("ip",hm.get("ip"));
													operations.setSufiConfigOnDb(param);
													fileLogger.debug("@vishal10");
													fileLogger.debug("@vishal@sunil10");
													//send antenna over udp
													boolean isSucceded = true;
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
														DBDataService.isInterrupted = true;
														return false;
													}else{
															isSucceded = switchAntena(band3g,"3G","8",sectorId);
															fileLogger.debug("@vishal11");
													}
	
													
													if(!isSucceded) 
													{
														if (DBDataService.rotatedMode==2)
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:UMTS\"}");
														else
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:UMTS\"}");
														fileLogger.debug("@vishal15");	
														if(TrackingcontinueuponFailure==1) {
															break;
														}
														else
															return false;
													}else
													{
														isSucceded = true;
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
															DBDataService.isInterrupted = true;
															//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
									                        return false;
														}else{
																isSucceded = switchAntena(band3g+"-NA","3G","7",sectorId);													
														}

														if(!isSucceded){
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:GSM\"}");
															if(TrackingcontinueuponFailure==1) {
	
																break;
															}
															else
																return false;
															
														}else{
															
														}
													}
													fileLogger.debug("@vishal16");
													//unlock the current device
													boolean lockStatus=false; 
													try{
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
														//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
									                    return false;
													}else{
														lockStatus= threegOperations.setCellLockUnlockDdp(data);	
													}
														LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
													    log.put("IP",hm.get("ip"));
														log.put("Action","UNLOCK");
														log.put("UARFCN",auditUarfcn);
														log.put("PSC",auditPsc);
														log.put("TECH",hm.get("typename"));
														log.put("Sector",antennaProfileName);
													
													   if(!lockStatus){
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
															new DeviceStatusServer().sendText("ok");
															log.put("ERROR","Connection Error on Unlocking");
															auditHandler.auditConfigExt(log);
															if(TrackingcontinueuponFailure==1) {
																error_flag=true;
																prev_ip= hm.get("ip");
																break;
															}
															else
																return false;
															
													   }else{
														   //new DeviceStatusServer().sendText(hm.get("ip"));
														   updateDeviceStatus("unlock",hm.get("ip"));
														   new DeviceStatusServer().sendText("ok");
															auditHandler.auditConfigExt(log);
															
													   }
													
												}catch(Exception e){
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
															DBDataService.isInterrupted = true;
															new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
															fileLogger.error("@interrupt 10 interrupted exception occurs");
															return false;
														}else{
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
														}
														if(TrackingcontinueuponFailure==1) {
															error_flag=true;
															prev_ip= hm.get("ip");
															break;
														}
														else
															return false;

													}
													fileLogger.debug("@vishal17");
												}
												else
												{
													fileLogger.debug("@vishal18");
													fileLogger.debug("dummy packet creation in 3G for synchronization with other technologies");	
												}
											}
											catch(Exception E)
											{
												fileLogger.error("exception:"+E.getMessage());
												
												fileLogger.debug("@vishal19");
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
													DBDataService.isInterrupted = true;
													//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
													new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
													fileLogger.error("@interrupt 11 interrupted exception occurs");
													return false;
												}
												if(TrackingcontinueuponFailure==1) {
													break;
												}
												else
													return false;
												
											}
										        
												if(!tempJsonObjectFor3g.has("dummyCheck")) 
												{
													break;
													
													
													
//													fileLogger.debug("@vishal20");
//											        //Thread.sleep(tempJsonObjectFor3g.getInt("duration")*60*1000);
//											        
//													fileLogger.debug("@sleep in 3g cue");
//													if(trackTime==-1){
//														fileLogger.debug("@Hey NEW TIME HOLD 7 3g :"+((operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value"))+holdTimeint)*1000);
//														Thread.sleep(((operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value"))+holdTimeint)*1000);
//													}else{
//														fileLogger.debug("@Hey NEW TIME HOLD 8 3g :"+((trackTime*60)+holdTimeint)*1000);
//														Thread.sleep(((trackTime*60)+holdTimeint)*1000);
//														
//													}
//														data.put("cmdType", "SET_CELL_LOCK");
//													data.put("data", "{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
//													try{
//														boolean unlockStatus=false;
//														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
//															DBDataService.isInterrupted = true;
//															//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
//									                        return false;
//														}else{
//															unlockStatus=threegOperations.setCellLockUnlockDdp(data);	
//														}
//															
//														   LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
//														   log.put("IP",hm.get("ip"));
//														   log.put("Action","LOCK");
//														   log.put("UARFCN",auditUarfcn);
//														   log.put("PSC",auditPsc);
//														   if(!unlockStatus){
//																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
//																new DeviceStatusServer().sendText("ok");
//																log.put("ERROR","Connection Error on Locking");
//																auditHandler.auditConfigExt(log);
//																if(TrackingcontinueuponFailure==1) {
//																	error_flag=true;
//																	prev_ip= hm.get("ip");
//																	continue;
//																}
//																else
//																	return false;
//																//retrun false;;
//														   }else{
//															   //new DeviceStatusServer().sendText(hm.get("ip"));
//															   updateDeviceStatus("lock",hm.get("ip"));
//															   new DeviceStatusServer().sendText("ok");
//															   auditHandler.auditConfigExt(log);
//																
//														   }
//														}catch(Exception e){
//															if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
//																DBDataService.isInterrupted = true;
//																//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
//																new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
//																fileLogger.error("@interrupt 12 interrupted exception occurs");
//																return false;
//															}else{
//																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
//															}
//															if(TrackingcontinueuponFailure==1) {
//																error_flag=true;
//																prev_ip= hm.get("ip");
//																continue;
//															}
//															else
//																return false;
//															//retrun false;;
//														}
//											        fileLogger.debug("@vishal21");
											}
												if(sysManagerAvailability){
												//switchDsp("neither");
												}
									 }
								}
						}
						fileLogger.debug("@vishal4g length:"+JSONArrayFor4g.length()+":4G NODES:"+devicesMapOverTech.get("4G").size());

						
						if(JSONArrayFor4g.length()>0 && devicesMapOverTech.get("4G").size()>0)
						{
							ArrayList<HashMap<String, String>> ArrayFor4gdevice=devicesMapOverTech.get("4G");
							for(int k=0;k<ArrayFor4gdevice.size();k++) {
								 
								 for(int n=0;n<JSONArrayFor4g.length();n++) {
									 
									 if(device.containsKey("4G-"+n))
									 {
										 continue;
									 }
									 
							
								 	running_4g=true;
						    
								 	FourgOperations fourgOperations=new FourgOperations();
								 	fileLogger.debug("@vishal4g1");
								 	HashMap<String,String> hm=devicesMapOverTech.get("4G").get(k);
								 	
								 	LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
								 	fileLogger.debug("@vishal4g2");
								 	data.put("cmdType", "SET_CELL_UNLOCK");
								 	data.put("systemCode", hm.get("dcode"));
									data.put("systemId", hm.get("sytemid"));
									data.put("systemIP", hm.get("ip"));
									fileLogger.debug("@vishal4g3");
									JSONObject tempJsonObjectFor4g=JSONArrayFor4g.getJSONObject(n);
									fileLogger.debug("4g packet:"+tempJsonObjectFor4g.toString());
									fileLogger.debug("@vishal4g4");
									String cmdForDsp = "";
									int l1_attn=-999;
									try{
										fileLogger.debug("@vishal4g5:"+tempJsonObjectFor4g.toString());
											if(!tempJsonObjectFor4g.has("dummyCheck"))
											{
												gone_in_2g_3g_4g =true;
												device.put("4G-"+n, 1);
												JSONArray compPacketArr=tempJsonObjectFor4g.getJSONArray("data");
												String packEarfcn="";
												String packPci="";
												String packBand="";
												for(int packSize=0;packSize<compPacketArr.length();packSize++){
													JSONObject packObj=compPacketArr.getJSONObject(packSize);
													if(packObj.getString("flag").equals("self")){
														packEarfcn=packObj.getString("earfcn");
														packPci=packObj.getString("pci");
														packBand=packObj.getString("band");
														break;
													}
												}
												data.put("data", "{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\""+lteCellId+"\"}");
	
//										if(falconType.equalsIgnoreCase("octasic")){
//											if (sysManagerAvailability) {
//												    cmdForDsp = "fdd_lte";
//													try {
//														if (!checkFddOrTdd(packBand, null)
//																.get("type").equalsIgnoreCase("FDD")) {
//															cmdForDsp = "tdd_lte";
//															
//
//															boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
//															if(!cmdStatus){
//																new AutoOperationServer()
//																.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable failed:LTE\"}");
//																//return false;
//															}else{
//																fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
//																
//															}
//															
//															
//														} 
//													} catch (Exception e) {
//														fileLogger.error("Exception in setSufiConfig of class FourgOperations with message:" + e.getMessage());
//													}
//													
//													
//													String respData=switchDsp(cmdForDsp,0,0,l1_attn);
//													if(respData.equals("")){
//														new AutoOperationServer()
//														.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:LTE\"}");
//														return false;
//													}else{
//														fileLogger.debug("@sleep about to sleep in LTE System Manager Switching");
//														Thread.sleep(sysManWait);
//													}
//													fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
//													updateStatusOfBts4g(hm.get("ip"));
//												}
//												}
												auditEarfcn=packEarfcn;
												auditPci=packPci;
													fileLogger.debug("@vishal7");
														double caulatedFreq = 1;
													
													if(freq == -1) 
													{
														JSONArray cellConfigurationArr=tempJsonObjectFor4g.getJSONArray("data");
														
														
														for(int kk=0;kk<cellConfigurationArr.length();kk++){
															JSONObject tempJsonObjectForCell=cellConfigurationArr.getJSONObject(kk);
															if(tempJsonObjectForCell.getString("flag").equalsIgnoreCase("self")){
																
																String newDlearfcn=tempJsonObjectForCell.getString("earfcn");
																String  newUlearfcn = null;
																int dlEarfcnToUlearfcnFormula=new PossibleConfigurations().getFormulaForTheGivenEarfcn(Integer.parseInt(newDlearfcn));
																if(dlEarfcnToUlearfcnFormula!=0)
																{
																	newUlearfcn=Integer.toString(Integer.parseInt(newDlearfcn)+(dlEarfcnToUlearfcnFormula));
																	caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(Integer.parseInt(newUlearfcn),"4G");
																}	
															}
														}
													}
													
													//send antenna over udp
													boolean isSucceded = true;
													String band4g="";
		
													band4g=getSupportedBand("4G",packBand);
													
													fileLogger.debug("@vaibhav band4g="+band4g);
													//int powerLTE = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
													int powerLTE=-999;
													if(falconType.equalsIgnoreCase("standard")){
														powerLTE = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
													}else{
														//powerLTE = Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-4G-"+band4g +"-1"));
														
														//powerLTE = Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-4G-"+band4g +"-1"));
														powerSettingValue=getPowerFromPowerSetting("4G",band4g,caulatedFreq);
														powerLTE=powerSettingValue.get("power_setting");
														l1_attn=powerSettingValue.get("l1_att");
														
													}
													
													//if(falconType.equalsIgnoreCase("octasic")){
													
													String SystemManagerToSwitchDSP=hm.get("systemmanager");
														
														if(SystemManagerToSwitchDSP!=null){
															if (SystemManagerToSwitchDSP.equals("")==false  ) {
																	if (sysManagerAvailability) {
																    cmdForDsp = "fdd_lte";
																	try {
																		if (!checkFddOrTdd(packBand, null)
																				.get("type").equalsIgnoreCase("FDD")) {
																			cmdForDsp = "tdd_lte";
																			
			
																			boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
																			if(!cmdStatus){
																				new AutoOperationServer()
																				.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable failed:LTE\"}");
																				//return false;
																			}else{
																				fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
																				
																			}
																			
																			
																		} 
																	} catch (Exception e) {
																		
																		fileLogger.error("Exception in setSufiConfig of class FourgOperations with message:" + e.getMessage());
																	}
																	String respData="";
																	
																	
																	
																	if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag && !running_2g && !running_3g && tech_prev.equalsIgnoreCase(cmdForDsp) && attn_prev==l1_attn) {
																		error_flag=false;
																		int fddtddRestartCmd=-1;
																		if(cmdForDsp.equalsIgnoreCase("fdd_lte"))
																		{
																			fddtddRestartCmd=4;
																		}
																		else 
																		{
																			fddtddRestartCmd=5;
																			String testQuery="select  * from gpsdata where logtime > timezone('utc'::text, now() )- INTERVAL '10 sec';";
																			 JSONArray jo = operations.getJson(testQuery);
																			 fileLogger.debug("@Jo= query 's length TDD = "+jo.length());
																			 if(jo==null || jo.length()==0)
																			 {
																				 new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"No GPS Found Aborting TDD\"}");
																				 fileLogger.debug("@Inside JO after query so aborting tdd  ");
			
																				 break;
																			 
																			 }
																		}
																			respData=switchDsp("restart",fddtddRestartCmd,99,999,SystemManagerToSwitchDSP);
																		
																	}
																	else {
																		respData=switchDsp(cmdForDsp,0,0,l1_attn,SystemManagerToSwitchDSP);
																	}
																
																	if(respData.equals("")){
																		new AutoOperationServer()
																		.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:LTE\"}");
																		if(TrackingcontinueuponFailure==1) {
																			break;
																		}
																		else
																			return false;

																	}else{
																		fileLogger.debug("@sleep about to sleep in LTE System Manager Switching");
																		
																		Thread.sleep(sysManWait);
																	}
																	fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
																	updateStatusOfBts4g(hm.get("ip"));
																}
															}
														}
														
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													
													DBDataService.configurdTxPower=powerLTE;
												    DBDataService.configuredFreq=caulatedFreq;
		
												    DBDataService.IsRestartRequired= false;
												    
													distance=10;
													LinkedHashMap<String,String> param=operations.setParamsFor4gConfig(tempJsonObjectFor4g,hm.get("ip"),powerLTE,l1_attn);
													//LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"));
													//LinkedHashMap<String,String> param=null;
													boolean result=false;
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
														// AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
								                        return false;
													}else{
														result =  new Common().send4gConfigurationToNode(Integer.parseInt(param.get("adminState")),param);
		
														//String respData=switchDsp("restart",0,99);
														fileLogger.debug("@vishal8");	
													}

													if(!result) 
													{
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:LTE(EARFCN:"+packEarfcn+",PCI:"+packPci+")\",\"packet\":"+tempJsonObjectFor4g.toString()+"}");
														fileLogger.debug("@vishal9");
														if(TrackingcontinueuponFailure==1) {
															error_flag=true;
															attn_prev=l1_attn;
															prev_ip= hm.get("ip");
															tech_prev=cmdForDsp;
															break;
														}
														else
															return false;

													}else{
														new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:LTE(EARFCN:"+packEarfcn+",PCI:"+packPci+")\",\"packet\":"+tempJsonObjectFor4g.toString()+"}");
													}
													
													
													String respData="";
		
													if(DBDataService.IsRestartRequired)
													{
														
													
														respData= switchDsp("restart",getdspProcessId(cmdForDsp),99,999,SystemManagerToSwitchDSP);
														
														
														if(respData.equals("")){
															new AutoOperationServer()
															.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed(RESET) :LTE\"}");
															
														}else{
															fileLogger.debug("@sleep about to sleep in LTE System Manager Switching(RESET)");
															
															Thread.sleep(sysManWait);
															fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
															updateStatusOfBts4g(hm.get("ip"));
														}
														
													}
													
													
													param.put("ip",hm.get("ip"));
													operations.setEsufiConfigOnDb(param);
													fileLogger.debug("@vishal4g10");
													fileLogger.debug("@vishal4g@sunil4g10");
												    
													
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
														DBDataService.isInterrupted = true;
														return false;
													}else{
														if(falconType.equalsIgnoreCase("standard")){
															isSucceded = switchAntena(band4g,"4G","8",sectorId);
														}else{
															if(band4g.equals("2300") || band4g.equals("2600")){
																isSucceded = switchAntena(band4g,"4G","8",sectorId);
															}else{
																isSucceded = switchAntena(band4g,"3G","8",sectorId);
															}
														}
														fileLogger.debug("@vishal11");
													}
													
													if(!isSucceded) 
													{
														if (DBDataService.rotatedMode==2)
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:LTE\"}");
															else
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:LTE\"}");
														fileLogger.debug("@vishal15");	
														if(TrackingcontinueuponFailure==1) {
															break;
														}
														else
															return false;
														
													}else
													{
														
														if(!cmdForDsp.equalsIgnoreCase("tdd_lte")) 
														{
														
															isSucceded = true;
															if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
																DBDataService.isInterrupted = true;
																//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
																return false;
																
															}else{
																if(falconType.equalsIgnoreCase("standard")){
																	isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																}else{
																	
																	if(band4g.equals("2300") || band4g.equals("2600")){
																		isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																	}else{
																		isSucceded = switchAntena(band4g+"-NA","3G","7",sectorId);
			
																	}
																}													
															}
			
															
															if(!isSucceded){
																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"PA Switching failed:LTE\"}");
																if(TrackingcontinueuponFailure==1) {
																	break;
																}
																else
																	return false;
																
															}else{
																
															}
															
														}
													}
													fileLogger.debug("@vishal16");
													//unlock the current device
													boolean lockStatus=false; 
													try{
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
														//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
									                    return false;
													}else{
														Thread.sleep(1000);
														lockStatus = fourgOperations.setCellLockUnlockDdp(data);	
													}
													
														LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
														log.put("IP",hm.get("ip"));
														log.put("Action","UNLOCK");
														log.put("EARFCN",auditEarfcn);
														log.put("PCI",auditPci);
														log.put("TECH",hm.get("typename"));
														
														log.put("Sector",antennaProfileName);
														
													   if(!lockStatus){
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
															new DeviceStatusServer().sendText("ok");
															log.put("ERROR","Connection Error on Unlocking");
															auditHandler.auditConfigExt(log);
															if(TrackingcontinueuponFailure==1) {
																error_flag=true;
																attn_prev=l1_attn;
																prev_ip= hm.get("ip");
																tech_prev=cmdForDsp;
																break;
															}
															else
																return false;
															
													   }else{
														   //new DeviceStatusServer().sendText(hm.get("ip"));
														   updateDeviceStatus("unlock",hm.get("ip"));
														   new DeviceStatusServer().sendText("ok");
															
															auditHandler.auditConfigExt(log);
															
														if(cmdForDsp.equalsIgnoreCase("tdd_lte")) 
															{
																isSucceded = true;
																if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
																	DBDataService.isInterrupted = true;
																	//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
																	return false;
																	
																}else{
																	if(falconType.equalsIgnoreCase("standard")){
																		isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																	}else{
																		if(band4g.equals("2300") || band4g.equals("2600")){
																			isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																		}else{
																			isSucceded = switchAntena(band4g+"-NA","3G","7",sectorId);
				
																		}
																	}													
																}
				
																
																if(!isSucceded){
																	new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"PA Switching failed:LTE\"}");
																	if(TrackingcontinueuponFailure==1) {
																		break;
																	}
																	else
																		return false;
																	
																}else{
																	
																}
															}
															
														
													}
													
													}catch(Exception e){
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
															DBDataService.isInterrupted = true;
															new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
															fileLogger.error("@interrupt 10 interrupted exception occurs");
															return false;
														}else{
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
														}
														if(TrackingcontinueuponFailure==1) {
															error_flag=true;
															attn_prev=l1_attn;
															prev_ip= hm.get("ip");
															tech_prev=cmdForDsp;
															break;
														}
														else
															return false;
													
													}
													fileLogger.debug("@vishal17");
												}
												else
												{
													fileLogger.debug("@vishal18");
													fileLogger.debug("dummy packet creation in 4G for synchronization with other technologies");	
												}
											}
											catch(Exception E)
											{
												fileLogger.error("exception 4g:"+E.getMessage());
												
												fileLogger.debug("@vishal4g19");
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
													DBDataService.isInterrupted = true;
													//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
													new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
													fileLogger.error("@interrupt 4g interrupted exception occurs");
													return false;
												}
												if(TrackingcontinueuponFailure==1) {
													 break;
												}
												else
													return false;
												
											}
										        
												if(!tempJsonObjectFor4g.has("dummyCheck")) 
												{
													
													break;
													
											}
												if(sysManagerAvailability){
												//switchDsp("neither");
												}
								}
							}
							
							if(gone_in_2g_3g_4g==true)
							{
						
								Thread.sleep(sleepTimeAfter4gOver);
								gone_in_2g_3g_4g=false;
								
							}
							
							
						}
						
						
				}
				catch(Exception e) 
				{
					
					   //switchDsp("neither");
					   fileLogger.debug("@vishal23");
					   fileLogger.error("Exception in method trackMobileOnGivenPacketList :"+e.getMessage());
					   //fileLogger.debug("Thread.currentThread().isInterrupted() is:"+Thread.currentThread().isInterrupted());
					   fileLogger.debug("DBDataService.isInterrupted is:"+DBDataService.isInterrupted);
						if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
							DBDataService.isInterrupted = true;
							//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
							//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
							fileLogger.error("@interrupt 13 interrupted exception occurs");
							return false;
						}
						if(TrackingcontinueuponFailure==1) {
							continue;
						}
						else
							return false;
						
				}
				}
			
			fileLogger.debug("@vishal22");
	   }catch(Exception E){
		   fileLogger.debug("@vishal24");
		   fileLogger.error("Exception in method trackMobileOnGivenPacketList :"+E.getMessage());
			if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
				DBDataService.isInterrupted = true;
				//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
				new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
				fileLogger.error("@interrupt 14 interrupted exception occurs");
			}
		   return false;
		   
	   }finally{
		   fileLogger.debug("@vishal26 calling final");
		   lockUnlockAllDevices(devicesMapOverTech,1);
		   //new CommonService().updateStatusOfGivenBts("all");
		   //new DeviceStatusServer().sendText("all");
		   new DeviceStatusServer().sendText("ok");
		   fileLogger.debug("@vishal27 calling final done");
	   }	
	   //boolean updateStatus=common.executeDLOperation("update config_status set status=1");
	   fileLogger.info("Exit Function : autoTackMobileOnGivenPacketList");
	   return true;
		   }
   
   
   public boolean switchAntena(String band,String tech,String code) 
   {
	   fileLogger.info("Inside Function : switchAntena");
	   String query="select * from device_packets where tech = '"+tech+"' and band='"+band+"' limit 1";
	   JSONArray jo = new Operations().getJson(query);
	   
	   String query1="select * from view_btsinfo where code = "+code+" limit 1";
	   JSONArray jo1 = new Operations().getJson(query1);
	   fileLogger.debug("@vishal"+jo1);
	   if(jo1!=null && jo1.length()>=1){
	   try 
	   {
		   fileLogger.debug("@vishal : entering"+jo1.toString());
		   String cmd = jo.getJSONObject(0).getString("cmd");
		   String ip = jo1.getJSONObject(0).getString("ip");
		   fileLogger.debug("@vishal : entering2");
		   int port = 2000;		   
		   String key= "lnaport";
		   
		   if(tech.equalsIgnoreCase("3g")) 
		   {
			   key="paport";
		   }
		   
		   port = Integer.parseInt(new Common().getDbCredential().get(key));
		   String result2=new UdpServerClient().send(ip, port, cmd,3);
		   if(result2 == null||result2.equalsIgnoreCase("fail"))
		  // if(result2 == null)
		   {
			   return false;
		   }
		   
		   
	   } 
	   catch (JSONException e) 
	   {
			// TODO Auto-generated catch block
			
			return false;
	   }
	   fileLogger.info("Exit Function : switchAntena");
	   return true;
	   }else{
		   fileLogger.info("Exit Function : switchAntena");
		   return true;
	   }
   }
   
   
   
   
   public boolean switchAntena(String band,String tech,String code,int antenaId)  
   {
	   fileLogger.info("Inside Function : switchAntena");
	  
	   if (code == "8" && DBDataService.rotatedMode == 2 && (antenaId>0 && antenaId <4))
			   {  
		   fileLogger.info("Inside Function : switchAntena :: STRU so antenaId ="+antenaId);

				//JSONArray profileName = operations.getJson("select profile_name from antenna where id ="+antenaId);
				//String profileNameSaved = profileName.getJSONObject(0).getString("profile_name");
		   		String strustatus =DBDataService.checkantenatypeandmovesection(antenaId);
				if (strustatus.contains("success"))
				return true;
				else
					return false;
		   }else
		   {
	   fileLogger.debug("@switchAntena band"+band +",tech-"+ tech +",code-"+ code +",sector,-"+ antenaId);
	   String query="select * from switching_data where tech = '"+tech+"' and band='"+band+"' and antena_id = "+antenaId+" limit 1";
	   
	   if(tech.equalsIgnoreCase("scanner")) 
	   {
		   query="select * from switching_data where tech = '"+tech+"' and band='"+band+"' and antena_id = "+antenaId+" limit 1";   
	   }
	   
	   JSONArray jo = new Operations().getJson(query);
	   
	   String query1="select * from view_btsinfo where code = "+code+" limit 1";
	   JSONArray jo1 = new Operations().getJson(query1);
	   fileLogger.debug("@vishal"+jo1);
	   int systemTypeCode=DBDataService.getSystemType();
	   if(DBDataService.isOctasicPowerOn()){
	   if(jo1!=null && jo1.length()>=1 && (((systemTypeCode==1 || systemTypeCode==2) && code.equals("8")) || code.equals("7"))){
	   try 
	   {
		   fileLogger.debug("@switch : entering"+jo1.toString());
		   
		   String cmd = "";
		   if(systemTypeCode==2){
			   cmd = jo.getJSONObject(0).getString("cmd_integrated");
		   }else{
			   cmd = jo.getJSONObject(0).getString("cmd_standalone");
		   }
		   String ip = jo1.getJSONObject(0).getString("ip");
		   fileLogger.debug("@switch : entering2");
		   int port = 2000;		   
		   String key= "lnaport";
		   
		   if(tech.equalsIgnoreCase("3g")) 
		   {
			   key="paport";
		   }
		  
		   port = Integer.parseInt(new Common().getDbCredential().get(key));
		  
		   String result2 = new UdpServerClient().send(ip, port, cmd,3);
		   if(result2 == null||result2.equalsIgnoreCase("fail"))
		//   if(result2 == null)
		   {
			   return false;
		   }
		   
		   
	   } 
	   catch (JSONException e) 
	   {
		   fileLogger.error("@switch exception coming message :"+e.getMessage());
			// TODO Auto-generated catch block
			
			return false;
	   }
	   fileLogger.info("Exit Function : switchAntena");

	   return true;
	   }else{ 
		   fileLogger.info("Exit Function : switchAntena");

		   return true;
	   }
	   }else{
		   fileLogger.info("Exit Function : switchAntena");

		   return true;
	   }
	   
		   }
   }
   public HashMap<String,Integer> getPowerFromPowerSetting(String tech,String band,double freq) {
	   
	   
	     JSONArray js=null;
	     HashMap<String,Integer> powerSettingMap=new HashMap<String,Integer>();  

		 String query="select * from power_setting where tech='"+tech+ "' and band = '"+ band + "' and  subband_start <= "+freq +" and subband_stop >= "+freq + " ;"  ;
		 fileLogger.debug("query = " + query);
		 
	
		 js = operations.getJson(query);
		 if(js.length()==0) {
			 
			 query="select * from power_setting where tech='"+tech+ "' and band = '"+ band + "' and  subband_start  = 0 and subband_stop  = 0; "  ;
			 js = operations.getJson(query);
		 }
		 int power_setting=-999;
		 int l1_att=-999;
		 
		 try {
			fileLogger.debug("js.getJSONObject(0) = " + js.getJSONObject(0));
			power_setting = Integer.parseInt(js.getJSONObject(0).getString("power_setting"));
			l1_att = Integer.parseInt(js.getJSONObject(0).getString("l1_attn"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			fileLogger.debug("Exception power_setting ==  = " + e);
			
			
		} 
		 powerSettingMap.put("power_setting",power_setting);
		 powerSettingMap.put("l1_att",l1_att);
		 
		 fileLogger.debug("Power_Setting = " + power_setting);
		   return powerSettingMap;
   }
   /*
    * 
    * This function below is used to send command to SGN as well as used to do Octasic On Off 
    * 
    * */
   public boolean sendOctasicCmd(int nodeId, String nodeName,String cmdType) 
   {
	   
	   fileLogger.info("Inside Function : sendOctasicCmd");

	   fileLogger.debug("@sendOctasicCmd nodeName"+nodeName +",cmdType-"+ cmdType);
	   String query="select cmd_code from octasic_cmd_code where node = '"+nodeName+"' and cmd_type='"+cmdType+"'";
	   Operations operations = new Operations();
	   JSONArray jo = operations.getJson(query);
	   
	   String nodeQuery="select * from view_btsinfo where code = "+nodeId+" limit 1";
	   JSONArray jo1 = operations.getJson(nodeQuery);
	   fileLogger.debug("@vishal"+jo1);
	   if(jo1!=null && jo1.length()>=1){
	   try 
	   {
		   fileLogger.debug("@switch : entering"+jo1.toString());
		   
		   String cmd = "";
		   cmd = jo.getJSONObject(0).getString("cmd_code");
		   String ip = jo1.getJSONObject(0).getString("ip");
		   fileLogger.debug("@switch : entering2");
		   int port = 2000;		   
		   String key= "paport";
		   
		   port = Integer.parseInt(new Common().getDbCredential().get(key));
		   String result2=new UdpServerClient().send(ip, port, cmd,3);
		   if(result2 == null||result2.equalsIgnoreCase("fail")) 
		
		 //  if(result2 == null)
		   {
			   return false;
		   }
	   } 
	   catch (JSONException e) 
	   {
		   fileLogger.error("@switch exception coming message :"+e.getMessage());
			// TODO Auto-generated catch block
			
			return false;
	   }
	   fileLogger.info("Exit Function : sendOctasicCmd");
	   return true;
	   }else{
		   fileLogger.info("Exit Function : sendOctasicCmd");
		   return true;
	   }
   }   
   
   
   public void lockUnlockAllDevices(HashMap<String,ArrayList<HashMap<String,String>>> devicesMapOverTech,int type) 
   {

	   fileLogger.info("Inside Function : lockUnlockAllDevices");
	   AuditHandler auditHandler = new AuditHandler();
	   fileLogger.debug("@sunil1");
	   if(type == 1) 
	   {   
		   fileLogger.debug("@sunil2");
		   
		   for(int j=0;j<devicesMapOverTech.get("4G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("4G").get(j);
			   fileLogger.debug("@sunil3");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil4");
			   data.put("cmdType", "SET_CELL_LOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"1\"}");
			   
			   fileLogger.debug("@sunil5");
			   
			   try {
				//new ThreegOperations().setCellLock(data);
				boolean lockStatus=new FourgOperations().setCellLockUnlockDdp(data);
				if(lockStatus){
					   updateDeviceStatus("lock",hm.get("ip"));
					   new DeviceStatusServer().sendText("ok");
					   auditHandler.auditConfig(hm.get("ip"),"LOCK");
				}
				//auditHandler.auditConfig(hm.get("ip"),"LOCK");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				fileLogger.debug("Exception in sufi lock "+e.getMessage());
			}
			   fileLogger.debug("@sunil6");
		   }
		   
		   for(int j=0;j<devicesMapOverTech.get("3G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("3G").get(j);
			   fileLogger.debug("@sunil3");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil4");
			   data.put("cmdType", "SET_CELL_LOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   fileLogger.debug("@sunil5");
			   
			   try {
				//new ThreegOperations().setCellLock(data);
				boolean lockStatus=new ThreegOperations().setCellLockUnlockDdp(data);
				if(lockStatus){
					   updateDeviceStatus("lock",hm.get("ip"));
					   new DeviceStatusServer().sendText("ok");
					   auditHandler.auditConfig(hm.get("ip"),"LOCK");
				}
				//auditHandler.auditConfig(hm.get("ip"),"LOCK");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				fileLogger.debug("Exception in sufi lock "+e.getMessage());
			}
			   fileLogger.debug("@sunil6");
		   }
		   
			for(int j=0;j<devicesMapOverTech.get("2G").size();j++)
			{
					HashMap<String,String> hm=devicesMapOverTech.get("2G").get(j);
					String statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"1");
					fileLogger.debug("@sunil7");
					if(statusResponse!=null && !statusResponse.equals("")){
						updateDeviceStatus("lock",hm.get("ip"));
						new DeviceStatusServer().sendText("ok");
						auditHandler.auditConfig(hm.get("ip"),"LOCK");
					}

			}
	   }
	   else 
	   {
		   
		   for(int j=0;j<devicesMapOverTech.get("4G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("4G").get(j);
			   fileLogger.debug("@sunil8");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil9");
			   data.put("cmdType", "SET_CELL_UNLOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   
			   fileLogger.debug("@sunil10");
			   try {
				//new ThreegOperations().setCellUnlock(data);
				boolean unlockStatus=new ThreegOperations().setCellLockUnlockDdp(data);
				if(unlockStatus){
					updateDeviceStatus("unlock",hm.get("ip"));
					new DeviceStatusServer().sendText("ok");
				    auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
				}
				//auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
			} catch (Exception e) {
				fileLogger.error("Exception in sufi unlock "+e.getMessage());
			}
			   fileLogger.debug("@sunil11");
		   }
		   
		   for(int j=0;j<devicesMapOverTech.get("3G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("3G").get(j);
			   fileLogger.debug("@sunil8");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil9");
			   data.put("cmdType", "SET_CELL_UNLOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   
			   fileLogger.debug("@sunil10");
			   try {
				//new ThreegOperations().setCellUnlock(data);
				boolean unlockStatus=new ThreegOperations().setCellLockUnlockDdp(data);
				if(unlockStatus){
					updateDeviceStatus("unlock",hm.get("ip"));
					new DeviceStatusServer().sendText("ok");
				    auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
				}
				//auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
			} catch (Exception e) {
				fileLogger.error("Exception in sufi unlock "+e.getMessage());
			}
			   fileLogger.debug("@sunil11");
		   }
		   
			for(int j=0;j<devicesMapOverTech.get("2G").size();j++)
			{
					HashMap<String,String> hm=devicesMapOverTech.get("2G").get(j);
					String statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"2");
					fileLogger.debug("@sunil12");
					if(statusResponse!=null && !statusResponse.equals("")){
						updateDeviceStatus("unlock",hm.get("ip"));
						new DeviceStatusServer().sendText("ok");
						auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
					}
					//auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
			}
	   }
	   
	   fileLogger.debug("@sunil13"); 
	   fileLogger.info("Exit Function : lockUnlockAllDevices");
	   
   }
   
   
   public void lockUnlockAllDevicesPerTech(HashMap<String,ArrayList<HashMap<String,String>>> devicesMapOverTech,int type,String techType) 
   {
	   fileLogger.info("Inside Function : lockUnlockAllDevicesPerTech");
	   fileLogger.debug("@sunil1");
	   if(type == 1) 
	   {
		   
		   fileLogger.debug("@sunil2");
		   if(techType.equals("3G")){
		   for(int j=0;j<devicesMapOverTech.get("3G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("3G").get(j);
			   fileLogger.debug("@sunil3");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil4");
			   data.put("cmdType", "SET_CELL_LOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   fileLogger.debug("@sunil5");
			   
			   try {
				new ThreegOperations().setCellLock(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				fileLogger.error("Exception in sufi lock "+e.getMessage());
			}
			   fileLogger.debug("@sunil6");
		   }
		   }else{
		   
			for(int j=0;j<devicesMapOverTech.get("2G").size();j++)
			{
					HashMap<String,String> hm=devicesMapOverTech.get("2G").get(j);
					new CommonService().setLockUnlock(hm.get("ip"),"1");
					fileLogger.debug("@sunil7");
			}
		 }
	   }
	   else 
	   {
		   if(techType.equals("3G")){
		   for(int j=0;j<devicesMapOverTech.get("3G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("3G").get(j);
			   fileLogger.debug("@sunil8");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil9");
			   data.put("cmdType", "SET_CELL_UNLOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   
			   fileLogger.debug("@sunil10");
			   try {
				new ThreegOperations().setCellUnlock(data);
			} catch (Exception e) {
				fileLogger.error("Exception in sufi unlock "+e.getMessage());
			}
			   fileLogger.debug("@sunil11");
		   }
		   }else{
		   
			for(int j=0;j<devicesMapOverTech.get("2G").size();j++)
			{
					HashMap<String,String> hm=devicesMapOverTech.get("2G").get(j);
					new CommonService().setLockUnlock(hm.get("ip"),"2");
					fileLogger.debug("@sunil12");
			}
		   }
	   }
	   
	   fileLogger.debug("@sunil13"); 
	   fileLogger.info("Exit Function : lockUnlockAllDevicesPerTech");
	   
   }
   
   public HashMap<String,String> lockUnlockAllDevicesOnAutoTrack(HashMap<String,ArrayList<HashMap<String,String>>> devicesMapOverTech,int type) 
   {
	   fileLogger.info("Inside Function : lockUnlockAllDevicesOnAutoTrack");
	   AuditHandler auditHandler = new AuditHandler();
	   HashMap<String,String> statusMap= new HashMap<String,String>();
	   statusMap.put("status","success");
	   fileLogger.debug("@sunil1");
	   if(type == 1) 
	   {
		   
		   fileLogger.debug("@sunil2");
		   
		   for(int j=0;j<devicesMapOverTech.get("4G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("4G").get(j);
			   fileLogger.debug("@sunil3");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil4");
			   data.put("cmdType", "SET_CELL_LOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"1\"}");
			   
			   fileLogger.debug("@sunil5");
			   
			   try{
				   boolean lockStatus=new FourgOperations().setCellLockUnlockDdp(data);
				   if(!lockStatus){
					   statusMap.put("status","failure");
					   statusMap.put("ip",hm.get("ip"));
					   statusMap.put("tech","LTE");
					   return statusMap;  
				   }else{
					   auditHandler.auditConfig(hm.get("ip"),"LOCK");
				   }
				   fileLogger.debug("@sunil6");
			   }catch(Exception E){ 
				   statusMap.put("status","failure");
				   statusMap.put("ip",hm.get("ip"));
				   statusMap.put("tech","LTE");
				   return statusMap;
			   }
		   }
		   
		   for(int j=0;j<devicesMapOverTech.get("3G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("3G").get(j);
			   fileLogger.debug("@sunil3");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil4");
			   data.put("cmdType", "SET_CELL_LOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   fileLogger.debug("@sunil5");
			   
			   try{
				   boolean lockStatus=new ThreegOperations().setCellLockUnlockDdp(data);
				   if(!lockStatus){
					   statusMap.put("status","failure");
					   statusMap.put("ip",hm.get("ip"));
					   statusMap.put("tech","UMTS");
					   return statusMap;  
				   }else{
					   auditHandler.auditConfig(hm.get("ip"),"LOCK");
				   }
				   fileLogger.debug("@sunil6");
			   }catch(Exception E){ 
				   statusMap.put("status","failure");
				   statusMap.put("ip",hm.get("ip"));
				   statusMap.put("tech","UMTS");
				   return statusMap;
			   }
		   }
		   
			for(int j=0;j<devicesMapOverTech.get("2G").size();j++)
			{
					HashMap<String,String> hm=devicesMapOverTech.get("2G").get(j);
					String statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"1");
					if(statusResponse==null || statusResponse.equals("")){
					fileLogger.debug("@sunil7");
					statusMap.put("status","failure");
					statusMap.put("ip",hm.get("ip"));
					statusMap.put("tech","GSM");
					return statusMap;
					}else{
						auditHandler.auditConfig(hm.get("ip"),"LOCK");
					}
			}
	   }
	   else 
	   {
		   for(int j=0;j<devicesMapOverTech.get("4G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("4G").get(j);
			   fileLogger.debug("@sunil8");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil9");
			   data.put("cmdType", "SET_CELL_UNLOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   
			   fileLogger.debug("@sunil10");
			   try{
				   boolean unlockStatus=new FourgOperations().setCellLockUnlockDdp(data);
				   if(!unlockStatus){
					   statusMap.put("status","failure");
					   statusMap.put("ip",hm.get("ip"));
					   statusMap.put("tech","LTE");
					   return statusMap;  
				   }else{
					   auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
				   }
			   }catch(Exception E){ 
				   statusMap.put("status","failure");
				   statusMap.put("ip",hm.get("ip"));
				   statusMap.put("tech","LTE");
				   return statusMap;
			   }
			   fileLogger.debug("@sunil11");   
			   
		   }
		   
		   
		   for(int j=0;j<devicesMapOverTech.get("3G").size();j++)
		   {
			   HashMap<String,String> hm=devicesMapOverTech.get("3G").get(j);
			   fileLogger.debug("@sunil8");
			   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			   fileLogger.debug("@sunil9");
			   data.put("cmdType", "SET_CELL_UNLOCK");
			   data.put("systemCode", hm.get("dcode"));
			   data.put("systemId", hm.get("sytemid"));
			   data.put("systemIP",hm.get("ip"));
			   data.put("data","{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
			   
			   
			   fileLogger.debug("@sunil10");
			   try{
				   boolean unlockStatus=new ThreegOperations().setCellLockUnlockDdp(data);
				   if(!unlockStatus){
					   statusMap.put("status","failure");
					   statusMap.put("ip",hm.get("ip"));
					   statusMap.put("tech","UMTS");
					   return statusMap;  
				   }else{
					   auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
				   }
			   }catch(Exception E){ 
				   statusMap.put("status","failure");
				   statusMap.put("ip",hm.get("ip"));
				   statusMap.put("tech","UMTS");
				   return statusMap;
			   }
			   fileLogger.debug("@sunil11");   
			   
		   }
		   
			for(int j=0;j<devicesMapOverTech.get("2G").size();j++)
			{
					HashMap<String,String> hm=devicesMapOverTech.get("2G").get(j);
					new CommonService().setLockUnlock(hm.get("ip"),"2");
					fileLogger.debug("@sunil12");
					auditHandler.auditConfig(hm.get("ip"),"UNLOCK");
			}
	   }
	   
	   fileLogger.debug("@sunil13"); 
	   fileLogger.info("Exit Function : lockUnlockAllDevicesOnAutoTrack");
	   return statusMap;
   }
   
   public JSONArray getDeviceDetails(String ip)
   { 
	   fileLogger.info("Inside Function : getDeviceDetails");
	  	String query = "select * from view_btsinfo where ip = '"+ip+"'";
		fileLogger.debug(query);
		JSONArray rs =  new Operations().getJson(query);
		fileLogger.info("Exit Function : getDeviceDetails");
		return rs;
   }
   
   
   public JSONArray getDeviceDetails(String tech,String band)
   { 
	   fileLogger.info("Inside Function : getDeviceDetails");
	  	String query = "select * from device_packets where tech = '"+tech+"' and band='"+band+"'";
		
	  	fileLogger.debug(query);
		
		JSONArray rs =  new Operations().getJson(query);
		  fileLogger.info("Exit Function : getDeviceDetails");
		return rs;
   }

   
   public void insertNodStatusChange(int previousStatus,int newStausCode,String ip) 
   {  
	   fileLogger.info("Inside Function : insertNodStatusChange");
	   if(previousStatus != newStausCode) 
	   {
		   Common co = new Common();
		   
		   String query = "select max(id) id from oprrationdata";
		   statusLogger.debug(query);
			JSONArray rs =  new Operations().getJson(query);
			String id =null;
					
			try {
				id = rs.getJSONObject(0).getString("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				
			}
			
			if(id != null ) 
			{
				co.executeDLOperation("insert into status_log (device_ip,previous_status,current_status,operation_id) values('"+ip+"','"+previousStatus+"','"+newStausCode+"',"+id+")");
			}
			else 
			{
				co.executeDLOperation("insert into status_log (device_ip,previous_status,current_status) values('"+ip+"','"+previousStatus+"','"+newStausCode+"')");
			}
		   
	   }
	   fileLogger.info("Exit Function : insertNodStatusChange");
   }
   
   
   /*
    * This function will store alarms 
    * */
   public boolean storeAlarm(LinkedHashMap<String,String> msgdata,String type)
	{	
	   fileLogger.info("Inside Function : storeAlarm");
	   try
	   {	
		   	JSONObject jo = new JSONObject(msgdata.get("data"));
			fileLogger.debug(jo.toString());
			int id 		=-1;
			fileLogger.debug("type is :"+type);
			switch(type) 
			{
				case "scan":
					 id 		= jo.getInt("NS_ID");
					 new CurrentNetscanAlarm(jo);
				break;
				case "sufi":
					 id 		= jo.getInt("SUFI_ID");
				break;
			}
			
	        String cmdCode	= 	jo.getString("CMD_CODE");
	        Long tstmp		= 	jo.getLong("TIME_STAMP");
	        int orign 		= 	jo.getInt("ORIGIN");
	        int alarmType 	= 	jo.getInt("ALARM_TYPE");
	        int cause 		= 	jo.getInt("CAUSE");
	        String desc 	= 	jo.getString("DESCRIPTION");
	        
	        Common co  		= new Common();
			String query ="insert into alarms (cmd_code, tstmp, origin, sufi_id, alarm_type, cause, description) values"
					+ "('"+cmdCode+"',"+tstmp+","+orign+","+id+","+alarmType+","+cause+",'"+desc+"') returning aid";
			
			int aid=co.executeQueryAndReturnId(query);
			String query1 = "select * from view_alarms where aid = ("+aid+");";
			new AlarmServer().sendText((new Operations().getJson(query)).toString());
				
		}
		catch(Exception E)
		{
			fileLogger.error("Parsing Json Alarms Data Exception msg : "+E.getMessage());
			return false;
		}
	   fileLogger.info("Exit Function : storeAlarm");
   		return true;
	}
   
   /*
    * This function will store alarms 
    * */
   public boolean storeSystemManagerAlarm(LinkedHashMap<String,String> msgdata,String type)
	{
	   fileLogger.info("Inside Function : storeSystemManagerAlarm");
	   try
	   {	
		   	JSONObject jo = new JSONObject(msgdata.get("data"));
			fileLogger.debug(jo.toString());
			fileLogger.debug("type is :"+type);
			
	        String cmdCode	= jo.getString("CMD_CODE");
	        String componentId = jo.getString("COMPONENTID");  
	        int componentType = jo.getInt("COMPONENTTYPE"); 
	        String managedObjectId = jo.getString("MANAGEDOBJECTID"); 
	        int managedObjectType = jo.getInt("MANAGEDOBJECTTYPE"); 
	        int eventId = jo.getInt("EVENTID"); 
	        String eventType = jo.getString("EVENTTYPE"); 
	        int severity  = jo.getInt("SEVERITY");
	        String eventDesc = jo.getString("EVENTDESCRIPTION"); 
	        Long genTime = jo.getLong("GENERATIONTIME");
	        
	        Common co  		= new Common();
			String insertQuery ="INSERT INTO alarm_data(node_type, cmd_code, comp_id, comp_type, man_obj_id, man_obj_type,event_id, event_type, severity, event_desc, gen_time) VALUES ('"+type+"','"+cmdCode+"','"+componentId+"',"+componentType+",'"+managedObjectId+"',"+managedObjectType+","+eventId+",'"+eventType+"','"+severity+"','"+eventDesc+"',"+genTime+") returning id";
			int id=co.executeQueryAndReturnId(insertQuery);
			fileLogger.debug("alarm_data id is :"+id);
			String fetchQuery = "select * from alarm_data where id = ("+id+");";
			JSONObject alarmData = new Operations().getJson(fetchQuery).getJSONObject(0);
			new AlarmDataServer().sendText(alarmData.toString());
			if(eventType.equalsIgnoreCase("TEMP_THRESHOLD_EXCEEDED")){
				
				   LinkedHashMap < String, String > faultLog = new LinkedHashMap < String, String > ();
				   faultLog.put("cmd code", cmdCode);
				   faultLog.put("event type", eventType);
				   faultLog.put("severity", "" + severity);
				   faultLog.put("event description",eventDesc);
				   new AuditHandler().auditFaultLog(faultLog);
				//send the alarm to the dashboard
				//stop all operations
				//Power off the OCTASIC supply
				DBDataService.setOctasicPowerOn(false);
				sendOctasicCmd(7, "System Manager", "power_off");
				//wait for some time
				Long octasicPowerOffToOnTime=Long.parseLong(DBDataService.configParamMap.get("octasic_off_on_time"));
				fileLogger.debug("octasicPowerOffToOnTime is :"+octasicPowerOffToOnTime);
				DBDataService.setOctasicPowerOn(true);
				try {
					fileLogger.debug("about to go to timer of octasic power off");
					
					Thread.sleep(octasicPowerOffToOnTime);
				} catch (Exception e) {
					fileLogger.error("exception in sleep with message :"+e.getMessage());
					// TODO Auto-generated catch block
					
				}
				//Power on the OCTASIC supply
				sendOctasicCmd(7, "System Manager", "power_on");
				//remove the alarm from the dashboard
				alarmData.put("status","c");
				new AlarmDataServer().sendText(alarmData.toString());
			}
				
		}
		catch(Exception E)
		{
			fileLogger.error("Parsing Json Alarms Data Exception msg : "+E.getMessage());
			return false;
		}
	   fileLogger.info("Exit Function : storeSystemManagerAlarm");
   		return true;
	}
   
   public boolean OctasicPowerOffOn(String systemManagerIPPassed)
  	{

	   boolean sendOctasicCmd2=false;
	   
		//JSONArray systemManagerArray = new Operations().getJson("select * from view_btsinfo where code=10 limit 1");
		String systemManagerIP="";
		//if(systemManagerArray!=null && systemManagerArray.length()>=1){
			try {
				//systemManagerIP = systemManagerArray.getJSONObject(0).getString("ip");
				systemManagerIP = systemManagerIPPassed;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			//netscanDspMap.put("systemIP", systemManagerObject.getString("ip"));
		//}
	   
	   fileLogger.info("Inside Function : OctasicPowerOffOn");
	   
	   try
	   {
				//send the alarm to the dashboard
				//stop all operations
				//Power off the OCTASIC supply
				//DBDataService.setOctasicPowerOn(false);
				fileLogger.debug("Command sending to sendOctasicCmd for poweroff");
				sendOctasicCmd2=sendOctasicCmd(7, "System Manager", "power_off");
				
				if(!sendOctasicCmd2){
					fileLogger.debug("Command Failed at switch during poweroff");
					return false;
				}
				//wait for some time
				Long octasicPowerOffToOnTime=Long.parseLong(DBDataService.configParamMap.get("octasic_off_on_time"));
				new TwogOperations().updateStatusOfBts(systemManagerIP);
				
				int OctasicWaitBeforeSwitchingOnTime=Integer.parseInt(DBDataService.configParamMap.get("octasicBootUpTime"));
				fileLogger.debug("octasicPowerOffToOnTime is :"+octasicPowerOffToOnTime);
				try {
					fileLogger.debug("about to go to timer of octasic power off");
					
					Thread.sleep(octasicPowerOffToOnTime);
				} catch (Exception e) {
					fileLogger.error("exception in sleep with message :"+e.getMessage());
					// TODO Auto-generated catch block
					
				}
				//Power on the OCTASIC supply
				fileLogger.debug("Command sending to sendOctasicCmd for poweron");
				sendOctasicCmd2=sendOctasicCmd(7, "System Manager", "power_on");
				if(!sendOctasicCmd2){
					fileLogger.debug("Command Failed at switch during poweron ");
					return false;
				}
				try {
					fileLogger.debug("about to go to timer of OctasicWaitBeforeSwitchingOnTime power off/on");
					
					Thread.sleep(OctasicWaitBeforeSwitchingOnTime);
				} catch (Exception e) {
					fileLogger.error("exception in sleep with message :"+e.getMessage());
					// TODO Auto-generated catch block
					
				}
				//DBDataService.setOctasicPowerOn(true);
				//remove the alarm from the dashboard
				
			
				
		}
		catch(Exception E)
		{
			fileLogger.error("Parsing Json Alarms Data Exception msg : "+E.getMessage());
			return false;
		}
	    finally {
	    	
	    	//DBDataService.setOctasicPowerOn(true);
	    }
	    fileLogger.info("Exit Function : storeSystemManagerAlarm");
		return true;

   
  	}
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   /***
    * Respose msg creator
    * 
    */
   
   
   public Response createResponseMsg(boolean result,String msg) 
   {
	   
	  // fileLogger.info("Inside Function : createResponseMsg");
	   if(msg == null) 
	   {
		   msg = "OK";
	   }
	   if(result) 
	   {
		   return Response.status(201).entity("{\"result\":\"success\",\"msg\":\""+msg+"\"}").build();
	   }
	  // fileLogger.info("Exit Function : createResponseMsg");
	   return Response.status(201).entity("{\"result\":\"success\",\"msg\":\""+msg+"\"}").build();   
   }
   
   public Response setGpsDataRequest(String systemCode,String systemId,String ip) 
   {   
	   fileLogger.info("Inside Function : setGpsDataRequest");
	   fileLogger.debug("@gps in setgpsdatarequest");
	   String gpsReportReuestCommand = "{\"CMD_CODE\": \"GET_SCAN_REPORT\"}";
	   
	   
	   
	   LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
	   data.put("cmdType", "GET_SCAN_REPORT");
	   data.put("systemCode",systemCode);
	   data.put("systemId",systemId);
	   data.put("data",gpsReportReuestCommand);
	   data.put("systemIP",ip);
	   fileLogger.info("Exit Function : setGpsDataRequest");
		
	   return new NetscanOperations().sendToServer(data);
	   
   }
   
   public void SetNodeStatusToNotReachable() 
   {
	  // fileLogger.info("Inside Function : SetNodeStatusToNotReachable");
	   String query  = "update btsmaster set status = 2";
	   new Common().executeDLOperation(query);
	  // fileLogger.info("Exit Function : SetNodeStatusToNotReachable");
   }
   
   public boolean updateActiveTrackingAntenna(int antennaId){
	  // fileLogger.info("Inside Function : updateActiveTrackingAntenna");
	   Common common = new Common();
	  // fileLogger.info("Exit Function : updateActiveTrackingAntenna");
	   return common.executeDLOperation("update active_antenna set antenna_id="+antennaId+" where opr_type='tracking'");
   }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   public boolean AutomanualSequential(String configData,int freq,int antennaId,int trackTime,boolean sysManagerAvailability,boolean switchAvailability,int repititionFreq)
   {
   
   
   
   

   
   fileLogger.debug("Inside Function : autoManualTackMobileOnGivenPacketList");
   boolean error_exists_tracking= false; 
   int attn_prev=-1;
   String tech_prev="";
   boolean error_flag=false;
   
   
   
   
   
   
   int holdTime= Integer.parseInt(DBDataService.configParamMap.get("HoldTime"));
   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTime 1wa =  "+holdTime);
   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTime 2wa =  "+DBDataService.configParamMap.get("addition_time"));
   
   int holdTimeint=holdTime;	
   if(holdTime!=0)
   {
	   
	    holdTimeint+=Integer.parseInt(DBDataService.configParamMap.get("addition_time"));
   }
   
   
   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTimeint =  "+holdTimeint);
   
   
   
   
   
   
   
   //String tracktimeUpdate="select value from system_properties where key='hold_time';";
   //int holdTimeint=0;
//   try {
//	  
//
//		   JSONArray js = new Operations().getJson(tracktimeUpdate);
//		
//		   holdTimeint=js.getJSONObject(0).getInt("value");
//		   fileLogger.error("Inside autoManualTackMobileOnGivenPacketList 2 - holdTimeint="+holdTimeint);
//	   
//   } catch (JSONException e1) {
//	// TODO Auto-generated catch block
//	e1.printStackTrace();
//   }
//	
//   if(holdTimeint>0) {
//	   
//	   trackTime=trackTime+holdTimeint+Integer.parseInt(DBDataService.configParamMap.get("addition_time"));
//   }
   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTimeint =  "+holdTimeint);
   fileLogger.debug("autoManualTackMobileOnGivenPacketList  trackTime =  "+trackTime);
   
   String prev_ip="";
   boolean running_2g=false;
   boolean running_3g=false;
   boolean running_4g=false;
   int TrackingcontinueuponFailure=Integer.parseInt(DBDataService.configParamMap.get("TrackingcontinueuponFailure"));
   
   fileLogger.debug("autoManualTackMobileOnGivenPacketList  TrackingcontinueuponFailure =  "+TrackingcontinueuponFailure);
   fileLogger.debug("in autoManualTackMobileOnGivenPacketList");
	long sysManWait=4000l;
	HashMap<String,Integer> powerSettingValue= new HashMap<String,Integer>();
	try{
		sysManWait=Long.parseLong(DBDataService.configParamMap.get("sysmanwait"));
		fileLogger.debug("@sysManWait is :"+sysManWait);
	}catch(Exception e){
		fileLogger.error("exception in getting sysManWait message :"+sysManWait);
	}
	
	//HashMap<String,String> powerSettingMap= new Common().getPowerSetting();
	//HashMap<String,String> powerSettingMap= new Common().getPowerSetting1();
	String falconType=DBDataService.configParamMap.get("falcontype");
	fileLogger.debug("@track falconType is :"+falconType);
   int sectorId = new CommonService().getSectorFromAntennaId(antennaId);
   updateActiveTrackingAntenna(antennaId);
   //switchAntenna(antennaId); 
   JSONObject configJson = null;
   AuditHandler auditHandler = new AuditHandler();
   Operations operations = new Operations();
   
   JSONArray json2gArray=new JSONArray();
   
   JSONArray json3gArray=new JSONArray();
   JSONArray json4gArray=new JSONArray();
   
   HashMap<String,ArrayList<HashMap<String,String>>> devicesMapOverTech = operations.getAllBtsInfoByTech();
   String auditArfcn="";
   String auditUarfcn="";
   String auditPsc="";
   String auditEarfcn="";
   String auditPci="";
   String lteCellId="1";
   
   try{
		String antennaProfileName = operations.getJson("select profile_name from antenna where id="+antennaId).getJSONObject(0).getString("profile_name");
		new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Started("+antennaProfileName+")\"}");
		new ScanTrackModeServer().sendText("track&Active&"+antennaProfileName);
		//JSONArray hummerDataArray = new Operations().getJson("select * from view_btsinfo where code = 12");
		//if(hummerDataArray.length()>0){
		//	new TRGLController().sendTrackingSectorToHummer(antennaProfileName);
	//	}
	   configJson = new JSONObject(configData);
	   
	   //final int repititionFreq = configJson.getInt("repitition_freq");
	   
	   JSONArray dataArray = configJson.getJSONArray("config_data");
	   
	   for(int i=0;i<dataArray.length();i++)
	   {
		   String tech=dataArray.getJSONObject(i).getString("tech");
		
		if(tech.equalsIgnoreCase("2g")){
			   json2gArray.put(dataArray.getJSONObject(i)); 
		   }else if(tech.equalsIgnoreCase("3g")){
			   json3gArray.put(dataArray.getJSONObject(i));
		   }else if(tech.equalsIgnoreCase("4g")){
			   json4gArray.put(dataArray.getJSONObject(i));
		   }else{
			   JSONArray tempJsonArr=dataArray.getJSONObject(i).getJSONArray("data");
			   for(int j=0;j<tempJsonArr.length();j++){
				 JSONObject tempJsonObj=tempJsonArr.getJSONObject(j); 
				 if(tempJsonObj.getString("flag").equalsIgnoreCase("self")){
					 if(!tempJsonObj.getString("arfcn").equalsIgnoreCase("")){
						 json2gArray.put(dataArray.getJSONObject(i));
						 fileLogger.debug("line 678 :"+json2gArray.length());
					 }else if(!tempJsonObj.getString("uarfcn").equalsIgnoreCase("")){
						 json3gArray.put(dataArray.getJSONObject(i));
						 fileLogger.debug("line 681 :"+json3gArray.length());
					 }else if(!tempJsonObj.getString("earfcn").equalsIgnoreCase("")){
						 json4gArray.put(dataArray.getJSONObject(i));
						 fileLogger.debug("line 684 :"+json4gArray.length());
					 }
				 }
			   }
		   }
	   }
	   
	   fileLogger.debug("2g array size :"+json2gArray.length());
	   fileLogger.debug("3g array size :"+json3gArray.length());
	   fileLogger.debug("4g array size :"+json4gArray.length());

	   ArrayList<JSONObject> json2gList=CommonService.getSortedList(json2gArray);
	   ArrayList<JSONObject> json3gList=CommonService.getSortedList(json3gArray);
	   ArrayList<JSONObject> json4gList=CommonService.getSortedList(json4gArray);
       
	   int json2gListSize=json2gList.size();
       int json3gListSize=json3gList.size();
       int json4gListSize=json4gList.size();
       
	   //int largeListSize=json2gListSize>=json3gListSize?json2gListSize:json3gListSize;
	   //int largeListSize=json2gListSize>=json3gListSize?(json2gListSize>=json4gListSize?json2gListSize:json4gListSize):(json4gListSize>=json3gListSize?json4gListSize:json3gListSize);
       
	   ArrayList<JSONObject> newJson2gList=new ArrayList<JSONObject>();
       ArrayList<JSONObject> newJson3gList=new ArrayList<JSONObject>();
       ArrayList<JSONObject> newJson4gList=new ArrayList<JSONObject>();
       
		/* **********************start**************************** */
		LinkedHashMap<String,PacketCount> packetMap = new LinkedHashMap<String,PacketCount>();
		  for(int i=0;i<json2gList.size();i++){
			  if(packetMap.get(json2gList.get(i).getString("plmn"))!=null){
				  PacketCount dt=packetMap.get(json2gList.get(i).getString("plmn"));
				  int count=dt.getCount2g();
				  if(count==0){
					  dt.setInitIndex2g(i);
				  }
				  count++;
				  dt.setCount2g(count);
			  }else{
				  PacketCount dt=new PacketCount(1,i,0,-1,0,-1);
				  packetMap.put(json2gList.get(i).getString("plmn"), dt);
			  }
		  }
		  for(int j=0;j<json3gList.size();j++){
			  if(packetMap.get(json3gList.get(j).getString("plmn"))!=null){
				  PacketCount dt=packetMap.get(json3gList.get(j).getString("plmn"));
				  int count=dt.getCount3g();
				  if(count==0){
					  dt.setInitIndex3g(j);
				  }
				  count++;
				  dt.setCount3g(count);
			  }else{
				  PacketCount dt=new PacketCount(0,-1,1,j,0,-1);
				  packetMap.put(json3gList.get(j).getString("plmn"), dt);
			  }
		  }
		  for(int k=0;k<json4gList.size();k++){
			  if(packetMap.get(json4gList.get(k).getString("plmn"))!=null){
				  PacketCount dt=packetMap.get(json4gList.get(k).getString("plmn"));
				  int count=dt.getCount4g();
				  if(count==0){
					  dt.setInitIndex4g(k);
				  }
				  count++;
				  dt.setCount4g(count);
			  }else{
				  PacketCount dt=new PacketCount(0,-1,0,-1,1,k);
				  packetMap.put(json4gList.get(k).getString("plmn"), dt);
			  }
		  }
		  fileLogger.debug("@Feb23 AutoManual Logs1 packetMap"+packetMap);
		   for(String hset:packetMap.keySet()){
			   PacketCount dt=packetMap.get(hset);
			   int count2g=dt.getCount2g();
			   int count3g=dt.getCount3g();
			   int count4g=dt.getCount4g();
			   
			   if(count2g>count3g){
				   if(count3g>count4g){
					   if(count2g>count4g){
						   //case 1
						   int i2g=dt.getInitIndex2g();
						   int i3g=dt.getInitIndex3g();
						   int i4g=dt.getInitIndex4g();
						   int i=0;
						   for(i=0;i<count4g;i++){
							   newJson2gList.add(json2gList.get(i2g++));
							   newJson3gList.add(json3gList.get(i3g++)); 
							   newJson4gList.add(json4gList.get(i4g++));
						   }
						   int j=i;
						   for(j=i;j<count3g;j++){
							   newJson2gList.add(json2gList.get(i2g));
							   newJson3gList.add(json3gList.get(i3g));
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json3gList.get(i).getString("plmn"));
						       jobj.put("duration",json3gList.get(i).getString("duration"));
							   newJson4gList.add(jobj);
							   fileLogger.debug("@Feb23 AutoManual Logs4 inside count3gwa");
							   i2g++;
							   i3g++;
						   }
						   for(int k=j;k<count2g;k++){
							   newJson2gList.add(json2gList.get(i2g));
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json2gList.get(i).getString("plmn"));
						       jobj.put("duration",json2gList.get(i).getString("duration"));
							   newJson3gList.add(jobj);
						       JSONObject jobj2= new JSONObject();
						       jobj2.put("dummyCheck","false");
						       jobj2.put("plmn",json2gList.get(i).getString("plmn"));
						       jobj2.put("duration",json2gList.get(i).getString("duration"));
							   newJson4gList.add(jobj2);
							   fileLogger.debug("@Feb23 AutoManual Logs4 inside count2gwa");
							   i2g++;
						   }
					   }else{
						   //case 2
						   //not applicable
					   }
				   }else{
					   if(count2g>count4g){
						   //case 3
						   int i2g=dt.getInitIndex2g();
						   int i3g=dt.getInitIndex3g();
						   int i4g=dt.getInitIndex4g();
						   int i=0;
						   for(i=0;i<count3g;i++){
							   newJson2gList.add(json2gList.get(i2g++));
							   newJson3gList.add(json3gList.get(i3g++)); 
							   newJson4gList.add(json4gList.get(i4g++));
						   }
						   int j=i;
						   for(j=i;j<count4g;j++){
							   newJson2gList.add(json2gList.get(i2g));
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json4gList.get(i).getString("plmn"));
						       jobj.put("duration",json4gList.get(i).getString("duration"));
							   newJson3gList.add(jobj);
							   newJson4gList.add(json4gList.get(i4g));
							   i2g++;
							   i4g++;
						   }
						   for(int k=j;k<count2g;k++){
							   newJson2gList.add(json2gList.get(i2g));
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json2gList.get(i).getString("plmn"));
						       jobj.put("duration",json2gList.get(i).getString("duration"));
							   newJson3gList.add(jobj);
						       JSONObject jobj2= new JSONObject();
						       jobj2.put("dummyCheck","false");
						       jobj2.put("plmn",json2gList.get(i).getString("plmn"));
						       jobj2.put("duration",json2gList.get(i).getString("duration"));
							   newJson4gList.add(jobj2);
							   i2g++;
						   }
					   }else{
						   //case 4
						   int i2g=dt.getInitIndex2g();
						   int i3g=dt.getInitIndex3g();
						   int i4g=dt.getInitIndex4g();
						   int i=0;
						   fileLogger.debug("@Feb23 AutoManual Logs4 inside else case 4 look code");
						   for(i=0;i<count3g;i++){
							   newJson2gList.add(json2gList.get(i2g++));
							   newJson3gList.add(json3gList.get(i3g++)); 
							   newJson4gList.add(json4gList.get(i4g++));
						   }
						   int j=i;
						   for(j=i;j<count2g;j++){
							   newJson2gList.add(json2gList.get(i2g));
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json2gList.get(i).getString("plmn"));
						       jobj.put("duration",json2gList.get(i).getString("duration"));
							   newJson3gList.add(jobj);
							   newJson4gList.add(json4gList.get(i4g));
							   fileLogger.debug("@Feb23 AutoManual Logs4 inside else case 4 look code 2g");
							   i2g++;
							   i4g++;
						   }
						   for(int k=j;k<count4g;k++){
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json4gList.get(i).getString("plmn"));
						       jobj.put("duration",json4gList.get(i).getString("duration"));
							   newJson2gList.add(jobj);
						       JSONObject jobj2= new JSONObject();
						       jobj2.put("dummyCheck","false");
						       jobj2.put("plmn",json4gList.get(i).getString("plmn"));
						       jobj2.put("duration",json4gList.get(i).getString("duration"));
						       fileLogger.debug("@Feb23 AutoManual Logs4 inside else case 4 look code 4g");
							   newJson3gList.add(jobj2);
							   newJson4gList.add(json4gList.get(i4g));
							   i4g++;
						   }
					   } 
				   }
			   }else{
				   if(count3g>count4g){
					   if(count2g>count4g){
						   //case 5
						   int i2g=dt.getInitIndex2g();
						   int i3g=dt.getInitIndex3g();
						   int i4g=dt.getInitIndex4g();
						   int i=0;
						   for(i=0;i<count4g;i++){
							   newJson2gList.add(json2gList.get(i2g++));
							   newJson3gList.add(json3gList.get(i3g++)); 
							   newJson4gList.add(json4gList.get(i4g++));
							   fileLogger.debug("@Feb23 AutoManual Logs4 inside count 3g>count4g and count2g > count4g");
						   }
						   int j=i;
						   for(j=i;j<count2g;j++){
							   newJson2gList.add(json2gList.get(i2g));
							   newJson3gList.add(json3gList.get(i3g));
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json2gList.get(i).getString("plmn"));
						       jobj.put("duration",json2gList.get(i).getString("duration"));
							   newJson4gList.add(jobj);
							   i2g++;
							   i3g++;
						   }
						   for(int k=j;k<count3g;k++){
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json3gList.get(i).getString("plmn"));
						       jobj.put("duration",json3gList.get(i).getString("duration"));
							   newJson2gList.add(jobj);
							   newJson3gList.add(json3gList.get(i3g));
						       JSONObject jobj2= new JSONObject();
						       jobj2.put("dummyCheck","false");
						       jobj2.put("plmn",json3gList.get(i).getString("plmn"));
						       jobj2.put("duration",json3gList.get(i).getString("duration"));
							   newJson4gList.add(jobj2);
							   i3g++;
						   }
					   }else{
						   //case 6
						   int i2g=dt.getInitIndex2g();
						   int i3g=dt.getInitIndex3g();
						   int i4g=dt.getInitIndex4g();
						   int i=0;
						   for(i=0;i<count2g;i++){
							   newJson2gList.add(json2gList.get(i2g++));
							   newJson3gList.add(json3gList.get(i3g++)); 
							   newJson4gList.add(json4gList.get(i4g++));
						   }
						   int j=i;
						   for(j=i;j<count4g;j++){
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json4gList.get(i).getString("plmn"));
						       jobj.put("duration",json4gList.get(i).getString("duration"));
							   newJson2gList.add(jobj);
							   newJson3gList.add(json3gList.get(i3g));
							   newJson4gList.add(json4gList.get(i4g));
							   i3g++;
							   i4g++;
						   }
						   for(int k=j;k<count3g;k++){
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json3gList.get(i).getString("plmn"));
						       jobj.put("duration",json3gList.get(i).getString("duration"));
							   newJson2gList.add(jobj);
							   newJson3gList.add(json3gList.get(i3g));
						       JSONObject jobj2= new JSONObject();
						       jobj2.put("dummyCheck","false");
						       jobj2.put("plmn",json3gList.get(i).getString("plmn"));
						       jobj2.put("duration",json3gList.get(i).getString("duration"));
							   newJson4gList.add(jobj2);
							   i3g++;
						   }
					   }
				   }else{
					   if(count2g>count4g){
						   //case 7
						   //not applicable
					   }else{
						   //case 8
						   int i2g=dt.getInitIndex2g();
						   int i3g=dt.getInitIndex3g();
						   int i4g=dt.getInitIndex4g();
						   int i=0;
						   for(i=0;i<count2g;i++){
							   newJson2gList.add(json2gList.get(i2g++));
							   newJson3gList.add(json3gList.get(i3g++)); 
							   newJson4gList.add(json4gList.get(i4g++));
						   }
						   int j=i;
						   for(j=i;j<count3g;j++){
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json3gList.get(i).getString("plmn"));
						       jobj.put("duration",json3gList.get(i).getString("duration"));
							   newJson2gList.add(jobj);
							   newJson3gList.add(json3gList.get(i3g));
							   newJson4gList.add(json4gList.get(i4g));
							   i3g++;
							   i4g++;
						   }
						   for(int k=j;k<count4g;k++){
						       JSONObject jobj= new JSONObject();
						       jobj.put("dummyCheck","false");
						       jobj.put("plmn",json4gList.get(i).getString("plmn"));
						       jobj.put("duration",json4gList.get(i).getString("duration"));
							   newJson2gList.add(jobj);
						       JSONObject jobj2= new JSONObject();
						       jobj2.put("dummyCheck","false");
						       jobj2.put("plmn",json4gList.get(i).getString("plmn"));
						       jobj2.put("duration",json4gList.get(i).getString("duration"));
							   newJson3gList.add(jobj2);
							   newJson4gList.add(json4gList.get(i4g));
							   i4g++;
						   }
					   } 
				   }
			   }
		   }
		  
		  
		/* **********************end**************************** */
       
/*	       for(int i=0;i<largeListSize;i++){
           if(i<json2gListSize && i<json3gListSize){
               if(json2gList.get(i).getString("plmn").equals(json3gList.get(i).getString("plmn"))){
            	   newJson2gList.add(json2gList.get(i));
            	   newJson3gList.add(json3gList.get(i));
               }else{
            	   newJson2gList.add(json2gList.get(i));
			       jobj.put("plmn",json2gList.get(i).getString("plmn"));
			       jobj.put("duration",json2gList.get(i).getString("duration"));
            	   newJson3gList.add(jobj);
            	   
            	   newJson3gList.add(json3gList.get(i));
			       jobj.put("plmn",json3gList.get(i).getString("plmn"));
			       jobj.put("duration",json3gList.get(i).getString("duration"));
                   newJson2gList.add(jobj);
               }
           }else if(i<json2gListSize && i>=json3gListSize){
        	   		newJson2gList.add(json2gList.get(i));
				    jobj.put("plmn",json2gList.get(i).getString("plmn"));
				    jobj.put("duration",json2gList.get(i).getString("duration"));
        	   		newJson3gList.add(jobj);
               
           }else if(i>=json2gListSize && i<json3gListSize){
        	   		newJson3gList.add(json3gList.get(i));
				    jobj.put("plmn",json3gList.get(i).getString("plmn"));
				    jobj.put("duration",json3gList.get(i).getString("duration"));
        	   		newJson2gList.add(jobj);
               
           }
           }*/
	   JSONArray  newJson2gArray = new JSONArray();
	   JSONArray  newJson3gArray = new JSONArray();
	   JSONArray  newJson4gArray = new JSONArray();
	   fileLogger.debug("@Feb23 AutoManual Logs2");
       for(int i=0;i<newJson2gList.size();i++){
    	   newJson2gArray.put(newJson2gList.get(i));
    	   newJson3gArray.put(newJson3gList.get(i));
    	   newJson4gArray.put(newJson4gList.get(i));
       }
	   
	   JSONArray JSONArrayFor2g=newJson2gArray;
	   JSONArray JSONArrayFor3g=newJson3gArray;
	   JSONArray JSONArrayFor4g=newJson4gArray;
	   

	   //setDbCounterValue("update config_status set start_time=now(),counter=0");
		//JSONArray jarr=getDbCounterValue("select start_time from config_status");
		//oprStartTime=jarr.getJSONObject(0).getString("start_time");
		
		int l1 = newJson2gArray.length();
		int l2 = newJson3gArray.length();
		int l3 = newJson4gArray.length();
		fileLogger.debug("@Feb23 AutoManual Logs3 l1="+l1);
		fileLogger.debug("@Feb23 AutoManual Logs3 l2="+l2);
		fileLogger.debug("@Feb23 AutoManual Logs3 l3="+l3);
		int countl1l2 = l1>l2?(l1>l3?l1:l3):(l3>l2?l3:l2);
		
		//int largeListSize=json2gListSize>=json3gListSize?(json2gListSize>=json4gListSize?json2gListSize:json4gListSize):(json4gListSize>=json3gListSize?json4gListSize:json3gListSize);
		
		new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configuration Packet : GSM "+json2gListSize+" : UMTS "+json3gListSize+" : LTE "+json4gListSize+" \"}");
		fileLogger.debug("Configuration Packet : GSM "+json2gListSize+" : UMTS "+json3gListSize+" : LTE "+json4gListSize);
		int iterationCount = 0;
/*			Date currDate = new Date();
	    Calendar calendar = Calendar. getInstance();
	    calendar.setTime(currDate);
	    calendar.add(Calendar.MINUTE,trackTime);
	    long futureTime=calendar.getTimeInMillis();*/
	    for(int repititionCount=0;repititionCount<repititionFreq;repititionCount++)
	    {
			//String query = "select round(extract(epoch from t_stoptime)*1000) as stoptime from oprrationdata order by id desc limit 1";
			//fileLogger.debug(query);
			//JSONArray rs =  new Operations().getJson(query);
			
/*				if(iterationCount != 0) 
			{
				//fileLogger.debug("curr and stop time:"+System.currentTimeMillis()+","+rs.getJSONObject(0).getLong("stoptime"));
				//fileLogger.debug("current time: "+System.currentTimeMillis()+" @vishal: "+rs.getJSONObject(0).getLong("stoptime"));
				fileLogger.debug("in while of autotrack");
				fileLogger.debug("futureTime is :"+futureTime+" and trackTime is :"+trackTime+"and current time is "+System.currentTimeMillis());
				if ( trackTime!=-1 && System.currentTimeMillis()>=futureTime) 
				{
					//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Done\"}");
					break;
				}
			}*/
			new CurrentOperation(Thread.currentThread());
			fileLogger.debug("@vishal1"+countl1l2);
			
			OperationCalculations oc = new OperationCalculations();
			
			
			
			int distance = new Operations().getJson("select value from system_properties where key='coverage'").getJSONObject(0).getInt("value");
			
			
			for(int i=0;i<countl1l2;i++)
			{
				
				//String query1 = "select round(extract(epoch from t_stoptime)*1000) as stoptime from oprrationdata order by id desc limit 1";
				//fileLogger.debug(query1);
				//JSONArray rs1 =  new Operations().getJson(query1);
				running_2g=false;
			    running_3g=false;
			    running_4g=false;
				if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
					DBDataService.isInterrupted = true;
                    return false;
				}else{
					if(falconType.equalsIgnoreCase("standard")){
					HashMap<String,String> statusMap=lockUnlockAllDevicesOnAutoTrack(devicesMapOverTech,1);
					if(statusMap.get("status").equals("failure")){
						new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+statusMap.get("ip")+"\"}");
						//new CommonService().updateStatusOfGivenBts("all");
						if(TrackingcontinueuponFailure==1) {
							continue;
						}
						else
							return false;
						//retrun false;;
					}else{
						updateDeviceStatus("lock","all");
						//new DeviceStatusServer().sendText("all");
						new DeviceStatusServer().sendText("ok");
					}
					}else{
					//lockUnlockAllDevices(devicesMapOverTech, 1);
						//not req in Octasic system
					}
				}

				fileLogger.debug("@vishal2"+countl1l2);
				try
				{
					
				
				if(JSONArrayFor2g.length()>0 && devicesMapOverTech.get("2G").size()>0)
				{
					
					
					if(falconType.equalsIgnoreCase("standard")){
						if(devicesMapOverTech.get("2G").size()>1){
							
						}
					}
						
						HashMap<String,String> hm=null;
							//HashMap<String,String> hm3g=devicesMapOverTech.get("3G").get(0);
								
						JSONObject tempJsonObjectFor2g=JSONArrayFor2g.getJSONObject(i);
						
					
						
							try
							{	
								fileLogger.debug("@vishal2g:"+tempJsonObjectFor2g.toString());
								
								
								if(!tempJsonObjectFor2g.has("dummyCheck"))
								{
									
									//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Applying Configuration for GSM\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
									
									
									double caulatedFreq = -1;
									if(freq == -1) 
									{
										JSONArray packetArrayFor2g = tempJsonObjectFor2g.getJSONArray("data");
										
										int length = packetArrayFor2g.length();
										for(int j=0;j<length;j++)
										{
											
											JSONObject tempObj = packetArrayFor2g.getJSONObject(j);
											
											if(tempObj.getString("flag").equalsIgnoreCase("self")) 
											{
												int arfcn = tempObj.getInt("arfcn");
												
												fileLogger.debug("@dani***************getting frequecny*******************");
												caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(arfcn,"2G");
												fileLogger.debug(arfcn);
												//Thread.sleep(60000);
												fileLogger.debug("@dani***************getting frequecny*******************");
												
												
											}
										}
									}
									else 
									{
										caulatedFreq = freq;
									}
									
									//int powerGSM = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
									String bandrr = "P_900";
									
									JSONArray compPacketArr=tempJsonObjectFor2g.getJSONArray("data");
									String packArfcn="";
									String packBand="";
									for(int packSize=0;packSize<compPacketArr.length();packSize++){
										JSONObject packObj=compPacketArr.getJSONObject(packSize);
										if(packObj.getString("flag").equals("self")){
											packArfcn=packObj.getString("arfcn");
											packBand=packObj.getString("band");
											
											break;
										}
									}
									String band2g="";
									/*if(packBand.equalsIgnoreCase("4"))
									{
										bandrr="DCS_1800";
									}*/
									
//									switch(packBand){
//									case "1":
//										band2g="850";
//										break;
//									case "2":
//										band2g="900";
//										break;
//									case "4":
//										band2g="1800";
//										break;
//									}
									
									band2g=getSupportedBand("2G",packBand);
									hm=getSupportedDevice(devicesMapOverTech,band2g);
									
									
									//vaibhav 11 dec 1:10 pm changed below
									
									
									
									
									
									if(falconType.equalsIgnoreCase("octasic")){
										String respData="";
										String SystemManagerToSwitchDSP="";
										JSONArray currentDeviceArray = new Operations().getJson("select * from btsmaster where ip = '" + hm.get("ip") + "' ;");
										SystemManagerToSwitchDSP=currentDeviceArray.getJSONObject(0).getString("systemmanager");
										//if (sysManagerAvailability) { earlier was this changed sanjay and vaibhav 13 April 21
										if (SystemManagerToSwitchDSP.equals("")==false && SystemManagerToSwitchDSP!=null)  {
											if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag) {
												error_flag=false;
												respData=switchDsp("restart",2,99,999,SystemManagerToSwitchDSP);
											}
											else{
												respData=switchDsp("gsm",0,0,999,SystemManagerToSwitchDSP);
											}
											
											if(respData.equals("")){
												new AutoOperationServer()
												.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:GSM\"}");
												if(TrackingcontinueuponFailure==1) {
													continue;
												}
												else
													return false;
	//retrun false;;
											}else{
												fileLogger.debug("@sleep about to sleep in GSM System Manager Switching");
												
												Thread.sleep(sysManWait);
											}
										}
										}
									
									
									
									
									
									
									
									
									
									
									
									
									fileLogger.debug("@vaibhav band2g="+band2g);
									int powerGSM=-999;
									
									int GSUUnit=0;
									//while(GSUUnit<devicesMapOverTech.get("2G").size()){
									//OIOIOI
									//}
									if(falconType.equalsIgnoreCase("standard")){
										powerGSM = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
									
									}else{
										//powerGSM=getPowerFromPowerSetting("2G",band2g,caulatedFreq);
										powerSettingValue=getPowerFromPowerSetting("2G",band2g,caulatedFreq);
										powerGSM=powerSettingValue.get("power_setting");
										

										//powerGSM = Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-2G-"+band2g +"-1"));
										
									}
									
									DBDataService.configurdTxPower=powerGSM;
								    DBDataService.configuredFreq=caulatedFreq;
									//send antina over udp
								    
									
									/*if(packBand.equalsIgnoreCase("4"))
									{
										bandrr="DCS_1800";
									}*/
									auditArfcn=packArfcn;
		
									String result="";
									if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
										DBDataService.isInterrupted = true;
										return false;
									}else{
										
										result = operations.locateWithNeighbour(tempJsonObjectFor2g,hm.get("ip"),powerGSM);
									}
									
									if(result == null || result.equalsIgnoreCase("{\"STATUS\":\"3\"}")) 
									{
										new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:GSM(ARFCN:"+packArfcn+")\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
										if(TrackingcontinueuponFailure==1) {
											error_flag=true;
											prev_ip= hm.get("ip");
											continue;
										}
										else
											return false;
//retrun false;;
									}else{
										new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:GSM(ARFCN:"+packArfcn+")\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
									}
									fileLogger.debug("@vishal2g1");
									
									operations.createNeighbourServerData(tempJsonObjectFor2g,hm.get("ip"),powerGSM);
									//send antina over udp
									//boolean isSucceded = switchAntena(tempJsonObjectFor2g.getJSONArray("data").getJSONObject(0).getString("band"),"2g","8");
									boolean isSucceded = true;
									if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
										DBDataService.isInterrupted = true;
				                        return false;
									}else{	
										
											if(falconType.equalsIgnoreCase("standard")){
												isSucceded = switchAntena(band2g,"2G","8",sectorId);
											}else{
												isSucceded = switchAntena(band2g,"3G","8",sectorId);
											}
									}
									//boolean isSucceded=true;
									if(!isSucceded) 
									{
										//String tmpmsg="Antenna Switching failed:GSM";
										if (DBDataService.rotatedMode==2)
											new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:GSM\"}");
										else
											new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:GSM\"}");
										if(TrackingcontinueuponFailure==1) {
											continue;
										}
										else
											return false;
//retrun false;;
									}
									else
									{
										isSucceded = true;
										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
											DBDataService.isInterrupted = true;
					                        return false;
										}else{	
												if(falconType.equalsIgnoreCase("standard")){
													isSucceded = switchAntena(band2g+"-NA","2G","7",sectorId);
												}else{
													isSucceded = switchAntena(band2g+"-NA","3G","7",sectorId);	
												}
										}
										if(!isSucceded){
											new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:UMTS\"}");
											if(TrackingcontinueuponFailure==1) {
												continue;
											}
											else
												return false;
//retrun false;;
										}
									}
									fileLogger.debug("@vishal2g2");
									String statusResponse="";
									if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
										DBDataService.isInterrupted = true;
				                        return false;
									}else{	
										
										statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"2");
									}
									
									
									LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
									
									log.put("IP",hm.get("ip"));
									log.put("Action","UNLOCK");
									log.put("ARFCN",auditArfcn);
									log.put("TECH",hm.get("typename"));
									
									log.put("Sector",antennaProfileName);
									log.put("ARFCN",auditArfcn);
									
									if(statusResponse==null || statusResponse.equals("")){
										new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
										//new DeviceStatusServer().sendText("ok");
										log.put("ERROR","Connection Error on Unlocking");
										auditHandler.auditConfigExt(log);
										if(TrackingcontinueuponFailure==1) {
											error_flag=true;
											prev_ip= hm.get("ip");
											continue;
										}
										else
											return false;
//retrun false;;

									}else{
										
										updateDeviceStatus("unlock",hm.get("ip"));
										new DeviceStatusServer().sendText("ok");
										//new DeviceStatusServer().sendText(hm.get("ip"));
										//auditlog start
										auditHandler.auditConfigExt(log);
										//auditlog end
									}
									fileLogger.debug("@vishal2g3");
								}
								else
								{
									fileLogger.debug("dummy packet creation in 2G for synchronization with other technologies");
								}
							}
							catch(Exception E)
							{
								fileLogger.error("@vishal exception in 2g packet thread :"+E.getMessage());
								if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
									DBDataService.isInterrupted = true;
									new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
									fileLogger.error("@interrupt  1 interrupted exception occurs");
									return false;
								}
								if(TrackingcontinueuponFailure==1) {
									
									continue;
								}
								else
									return false;
//retrun false;;
								

							}
						        
							if(!tempJsonObjectFor2g.has("dummyCheck")) 
							{
								fileLogger.debug("@vishal2g4");
								//fileLogger.debug("about to sleep in gsm configuration");
							    //Thread.sleep(tempJsonObjectFor2g.getInt("duration")*60*1000);
								//Thread.sleep(operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value"));
								fileLogger.debug("@sleep in 2g manual for time :"+tempJsonObjectFor2g.getLong("duration"));
								
								fileLogger.debug("@Hey NEW TIME HOLD 1 2g:"+((tempJsonObjectFor2g.getLong("duration"))+holdTimeint)*1000);
								Thread.sleep(((tempJsonObjectFor2g.getLong("duration"))+holdTimeint)*1000);
								
								
								String statusResponse="";
								if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
									DBDataService.isInterrupted = true;
			                        return false;
								}else{	
									statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"1");
								}
								
								LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
								log.put("IP",hm.get("ip"));
								log.put("Action","LOCK");
								log.put("ARFCN",auditArfcn);
								
								if(statusResponse==null || statusResponse.equals("")){
									new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
									//new DeviceStatusServer().sendText("ok");
									log.put("ERROR","Connection Error on Locking");
									auditHandler.auditConfigExt(log);
									if(TrackingcontinueuponFailure==1) {
										error_flag=true;
										prev_ip= hm.get("ip");
										continue;
									}
									else
										return false;
//retrun false;;
								}else{
											updateDeviceStatus("lock",hm.get("ip"));
											
									new DeviceStatusServer().sendText("ok");
									//new DeviceStatusServer().sendText(hm.get("ip"));
									//auditlog start
									auditHandler.auditConfigExt(log);
									//auditlog end
								}
							}	
				}
			
				fileLogger.debug("@vishal2.5"+JSONArrayFor3g.length()+""+devicesMapOverTech.get("3G").size());
				
					if(JSONArrayFor3g.length()>0 && devicesMapOverTech.get("3G").size()>0)
						{

						ThreegOperations threegOperations=new ThreegOperations();
						fileLogger.debug("@vishal3");
								HashMap<String,String> hm=devicesMapOverTech.get("3G").get(0);
								//HashMap<String,String> hm1=devicesMapOverTech.get("2G").get(0);
								
								LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
								fileLogger.debug("@vishal4");
								   data.put("cmdType", "SET_CELL_UNLOCK");
								   data.put("systemCode", hm.get("dcode"));
								   data.put("systemId", hm.get("sytemid"));
								   data.put("systemIP", hm.get("ip"));
								   data.put("data", "{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
								
								   fileLogger.debug("@vishal5");
								JSONObject tempJsonObjectFor3g=JSONArrayFor3g.getJSONObject(i);
									fileLogger.debug("line 879:"+tempJsonObjectFor3g.toString());
									fileLogger.debug("@vishal6");
									try{
										fileLogger.debug("@vishal3g:"+tempJsonObjectFor3g.toString());
										if(!tempJsonObjectFor3g.has("dummyCheck"))
										{
											if(falconType.equalsIgnoreCase("octasic")){
												String respData="";
												String SystemManagerToSwitchDSP="";
												JSONArray currentDeviceArray = new Operations().getJson("select * from btsmaster where ip = '" + hm.get("ip") + "' ;");
												SystemManagerToSwitchDSP=currentDeviceArray.getJSONObject(0).getString("systemmanager");
											
												if (SystemManagerToSwitchDSP.equals("")==false && SystemManagerToSwitchDSP!=null)  {
													if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag && !running_2g ) {
														error_flag=false;
														respData=switchDsp("restart",3,99,999,SystemManagerToSwitchDSP);	
													}
													else {
															respData=switchDsp("sufi",0,0,999,SystemManagerToSwitchDSP);
														}
													if(respData.equals("")){
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:UMTS\"}");
														if(TrackingcontinueuponFailure==1) {
															continue;
														}
														else
															return false;
														
//retrun false;;
													}else{
														fileLogger.debug("@sleep about to sleep in UMTS System Manager Switching");
														Thread.sleep(sysManWait);
													}
												}
												}	
										JSONArray compPacketArr=tempJsonObjectFor3g.getJSONArray("data");
										String packUarfcn="";
										String packPsc="";
										String packBand="";
										for(int packSize=0;packSize<compPacketArr.length();packSize++){
											JSONObject packObj=compPacketArr.getJSONObject(packSize);
											if(packObj.getString("flag").equals("self")){
												packUarfcn=packObj.getString("uarfcn");
												packPsc=packObj.getString("psc");
												packBand=packObj.getString("band");
												break;
											}
										}
										auditUarfcn=packUarfcn;
										auditPsc=packPsc;
										fileLogger.debug("@vishal7");
										double caulatedFreq = 1;
											
											if(freq == -1) 
											{
												JSONArray cellConfigurationArr=tempJsonObjectFor3g.getJSONArray("data");
												
												
												for(int kk=0;kk<cellConfigurationArr.length();kk++){
													JSONObject tempJsonObjectForCell=cellConfigurationArr.getJSONObject(kk);
													if(tempJsonObjectForCell.getString("flag").equalsIgnoreCase("self")){
														
														String newDluarfcn=tempJsonObjectForCell.getString("uarfcn");
														String  newUluarfcn = null;
														int dlUarfcnToUluarfcnFormula=new PossibleConfigurations().getFormulaForTheGivenUarfcn(Integer.parseInt(newDluarfcn));
														if(dlUarfcnToUluarfcnFormula!=0)
														{
															newUluarfcn=Integer.toString(Integer.parseInt(newDluarfcn)+(dlUarfcnToUluarfcnFormula));
															caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(Integer.parseInt(newDluarfcn),"3G");
														}	
													}
												}
											}
											
											String band3g="";
//											switch(packBand){
//											case "5":
//												band3g="850";
//												break;
//											case "8":
//												band3g="900";
//												break;
//											case "3":
//												band3g="1800";
//												break;
//											case "1":
//												band3g="2100";
//											}
											band3g=getSupportedBand("3G",packBand);
											fileLogger.debug("@vaibhav band3g="+band3g);
											
											//@mighty raju
											distance=10;
											//int powerUMTS = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
											//int powerUMTS=Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-3G-"+band3g +"-1"));
											//int powerUMTS=getPowerFromPowerSetting("3G",band3g,caulatedFreq);
											
											powerSettingValue=getPowerFromPowerSetting("3G",band3g,caulatedFreq);
											int powerUMTS=-999;
											powerUMTS=powerSettingValue.get("power_setting");
											
											DBDataService.configurdTxPower=powerUMTS;
										    DBDataService.configuredFreq=caulatedFreq;
											LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"),powerUMTS);
											//LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"));
											//LinkedHashMap<String,String> param=null;
											boolean result=true;
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
						                        return false;
											}else{
												result= new Common().sendConfigurationToNode(Integer.parseInt(param.get("adminState")),param);
											}
											fileLogger.debug("@vishal8");
											if(!result) 
											{
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:UMTS(UARFCN:"+packUarfcn+",PSC:"+packPsc+")\",\"packet\":"+tempJsonObjectFor3g.toString()+"}");
												fileLogger.debug("@vishal9");
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;
													prev_ip= hm.get("ip");
													continue;
												}
												else
													return false;
//retrun false;;
											}else{
												new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:UMTS(UARFCN:"+packUarfcn+",PSC:"+packPsc+")\",\"packet\":"+tempJsonObjectFor3g.toString()+"}");
											}
											param.put("ip",hm.get("ip"));
											operations.setSufiConfigOnDb(param);
											fileLogger.debug("@vishal10");
											fileLogger.debug("@vishal@sunil10");
											//send antenna over udp
											boolean isSucceded = true;
											
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
						                        return false;
											}else{	
													isSucceded = switchAntena(band3g,"3G","8",sectorId);
											}
											fileLogger.debug("@vishal11");
											if(!isSucceded) 
											{
												if (DBDataService.rotatedMode==2)
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:UMTS\"}");
												else
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:UMTS\"}");
												fileLogger.debug("@vishal15");	
												if(TrackingcontinueuponFailure==1) {
													continue;
												}
												else
													return false;
//retrun false;;
											}else
											{
												isSucceded = true;
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
													DBDataService.isInterrupted = true;
							                        return false;
												}else{
														isSucceded = switchAntena(band3g+"-NA","3G","7",sectorId);
												}
												if(!isSucceded){
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:GSM\"}");
													if(TrackingcontinueuponFailure==1) {
														continue;
													}
													else
														return false;
//retrun false;;
												}else{
													
												}
											}
											fileLogger.debug("@vishal16");
											//unlock the current device  
											try{
											boolean lockStatus=false;
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
						                        return false;
											}else{	
												lockStatus=threegOperations.setCellLockUnlockDdp(data);
											}
											LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
											log.put("IP",hm.get("ip"));
											log.put("Action","UNLOCK");
											log.put("UARFCN",auditUarfcn);
											log.put("PSC",auditPsc);
											log.put("TECH", hm.get("typename"));
											log.put("Sector",antennaProfileName);
											   if(!lockStatus){
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
													//new DeviceStatusServer().sendText("ok");
													log.put("ERROR","Connection Error on Unlocking");
													auditHandler.auditConfigExt(log);
													if(TrackingcontinueuponFailure==1) {
														error_flag=true;
														prev_ip= hm.get("ip");
														continue;
													}
													else
														return false;
//retrun false;;
											   }else{
												   updateDeviceStatus("unlock",hm.get("ip"));
												   new DeviceStatusServer().sendText("ok");
												  // new DeviceStatusServer().sendText(hm.get("ip"));
													//auditlog start
													auditHandler.auditConfigExt(log);
													//auditlog end
											   }
											
											}catch(Exception e){
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
													DBDataService.isInterrupted = true;
													new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
													fileLogger.error("@interrupt  2 interrupted exception occurs");
													return false;
												}else{
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
												}
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;
													prev_ip= hm.get("ip");
													continue;
												}
												else
													return false;
//retrun false;;
											}
											fileLogger.debug("@vishal17");
										}
										else
										{
											fileLogger.debug("@vishal18");
											fileLogger.debug("dummy packet creation in 3G for synchronization with other technologies");	
										}
									}
									catch(Exception E)
									{
										fileLogger.error("exception:"+E.getMessage());
										
										fileLogger.debug("@vishal19");
										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
											DBDataService.isInterrupted = true;
											new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
											fileLogger.debug("@interrupt  3 interrupted exception occurs");
											return false;
										}
										
										if(TrackingcontinueuponFailure==1) {
											
											continue;
										}
										else
											return false;
//retrun false;;
									}
								        
										if(!tempJsonObjectFor3g.has("dummyCheck")) 
										{
											fileLogger.debug("@vishal20");
									        //Thread.sleep(tempJsonObjectFor3g.getInt("duration")*60*1000);
									        
											
											//Thread.sleep(operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value"));
											fileLogger.debug("@sleep in 3g manual for :"+tempJsonObjectFor3g.getLong("duration"));
											fileLogger.debug("@Hey NEW TIME HOLD 2 3g :"+((tempJsonObjectFor3g.getLong("duration"))+holdTimeint)*1000);
											Thread.sleep(((tempJsonObjectFor3g.getLong("duration"))+holdTimeint)*1000);
											data.put("cmdType", "SET_CELL_LOCK");
											data.put("data", "{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
											try{
												boolean unlockStatus=false;
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
													DBDataService.isInterrupted = true;
							                        return false;
												}else{	
													unlockStatus=threegOperations.setCellLockUnlockDdp(data);
												}
												LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
												log.put("IP",hm.get("ip"));
												log.put("Action","LOCK");
												log.put("UARFCN",auditUarfcn);
												log.put("PSC",auditPsc);
												   if(!unlockStatus){
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
														//new DeviceStatusServer().sendText("ok");
														log.put("ERROR","Connection Error on Locking");
														auditHandler.auditConfigExt(log);
														if(TrackingcontinueuponFailure==1) {
															error_flag=true;
															prev_ip= hm.get("ip");
															continue;
														}
														else
															return false;
//retrun false;;
												   }else{
													   updateDeviceStatus("lock",hm.get("ip"));
													   new DeviceStatusServer().sendText("ok");
													   //new DeviceStatusServer().sendText(hm.get("ip"));
														//auditlog start
														auditHandler.auditConfigExt(log);
														//auditlog end
												   }
												}catch(Exception e){
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
														DBDataService.isInterrupted = true;
														//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
														new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
														fileLogger.error("@interrupt  4 interrupted exception occurs");
														return false;
													}else{
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
													}
													if(TrackingcontinueuponFailure==1) {
														error_flag=true;
														prev_ip= hm.get("ip");
														continue;
													}
													else
														return false;
//retrun false;;
												}
									        fileLogger.debug("@vishal21");
									}
										if(sysManagerAvailability){
										//switchDsp("neither");
										}
						}
					
					fileLogger.debug("@vishal4g length:"+JSONArrayFor4g.length()+":4G NODES:"+devicesMapOverTech.get("4G").size());
					
					if(JSONArrayFor4g.length()>0 && devicesMapOverTech.get("4G").size()>0)
					{
							 
								 
								 
								 
								 
						FourgOperations fourgOperations=new FourgOperations();
						fileLogger.debug("@vishal4g1");
						HashMap<String,String> hm=devicesMapOverTech.get("4G").get(0);
						//HashMap<String,String> hm1=devicesMapOverTech.get("2G").get(0);
						LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
						fileLogger.debug("@vishal4g2");
						data.put("cmdType", "SET_CELL_UNLOCK");
						data.put("systemCode", hm.get("dcode"));
						data.put("systemId", hm.get("sytemid"));
						data.put("systemIP", hm.get("ip"));
						fileLogger.debug("@vishal4g3");
						JSONObject tempJsonObjectFor4g=JSONArrayFor4g.getJSONObject(i);
						fileLogger.debug("4g packet:"+tempJsonObjectFor4g.toString());
						fileLogger.debug("@vishal4g4");
						String cmdForDsp ="";
						int l1_attn=-999;
						try{
							fileLogger.debug("@vishal4g5:"+tempJsonObjectFor4g.toString());
								if(!tempJsonObjectFor4g.has("dummyCheck"))
								{
									
									JSONArray compPacketArr=tempJsonObjectFor4g.getJSONArray("data");
									String packEarfcn="";
									String packPci="";
									String packBand="";
									
									for(int packSize=0;packSize<compPacketArr.length();packSize++){
										JSONObject packObj=compPacketArr.getJSONObject(packSize);
										if(packObj.getString("flag").equals("self")){
											packEarfcn=packObj.getString("earfcn");
											packPci=packObj.getString("pci");
											packBand=packObj.getString("band");
											break;
										}
									}
									data.put("data", "{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\""+lteCellId+"\"}");
									
									
//									if(falconType.equalsIgnoreCase("octasic")){
//										if (sysManagerAvailability) {
//											 cmdForDsp = "fdd_lte";
//												try {
//													if (!checkFddOrTdd(packBand, null)
//															.get("type").equalsIgnoreCase("FDD")) {
//														cmdForDsp = "tdd_lte";
//														
//														
//														boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
//														if(!cmdStatus){
//															new AutoOperationServer()
//															.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable CMD  failed:LTE\"}");
//															//return false;
//														}else{
//															fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
//															
//														}
//													} 
//												} catch (Exception e) {
//													fileLogger.error("Exception in setSufiConfig of class FourgOperations with message:" + e.getMessage());
//												}
//												
//
//												String respData=switchDsp(cmdForDsp,0,0,999);
//												if(respData.equals("")){
//													new AutoOperationServer()
//													.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:LTE\"}");
//													//return false;
//												}else{
//													fileLogger.debug("@sleep about to sleep in LTE System Manager Switching");
//													Thread.sleep(sysManWait);
//												}
//												fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
//												updateStatusOfBts4g(hm.get("ip"));
//											}
//											}
									auditEarfcn=packEarfcn;
									auditPci=packPci;
										fileLogger.debug("@vishal7");
											double caulatedFreq = 1;
										
										if(freq == -1) 
										{
											JSONArray cellConfigurationArr=tempJsonObjectFor4g.getJSONArray("data");
											
											
											for(int kk=0;kk<cellConfigurationArr.length();kk++){
												JSONObject tempJsonObjectForCell=cellConfigurationArr.getJSONObject(kk);
												if(tempJsonObjectForCell.getString("flag").equalsIgnoreCase("self")){
													
													String newDlearfcn=tempJsonObjectForCell.getString("earfcn");
													String  newUlearfcn = null;
													int dlEarfcnToUlearfcnFormula=new PossibleConfigurations().getFormulaForTheGivenEarfcn(Integer.parseInt(newDlearfcn));
													if(dlEarfcnToUlearfcnFormula!=0)
													{
														newUlearfcn=Integer.toString(Integer.parseInt(newDlearfcn)+(dlEarfcnToUlearfcnFormula));
														caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(Integer.parseInt(newUlearfcn),"4G");
													}	
												}
											}
										}
										
										//send antenna over udp
										boolean isSucceded = true;
										String band4g="";
										
//										switch(packBand){
//										case "1":
//											band4g="2100";
//											break;
//										case "3":
//											band4g="1800";
//											break;
//										case "5":
//											band4g="850";
//											break;
//										case "8":
//											band4g="900";
//											break;
//										/*case "40":
//											band4g="2300";
//										break;
//										case "41":
//											band4g="2600";*/
//										}
										
										band4g=getSupportedBand("4G",packBand);
										fileLogger.debug("@vaibhav band4g="+band4g);
										int powerLTE=-999;
										
										if(falconType.equalsIgnoreCase("standard")){
											powerLTE = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
										}else{

											powerSettingValue=getPowerFromPowerSetting("4G",band4g,caulatedFreq);
											powerLTE=powerSettingValue.get("power_setting");
											l1_attn=powerSettingValue.get("l1_att");
											
											//powerLTE = Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-4G-"+band4g +"-1"));
										}
									
										if(falconType.equalsIgnoreCase("octasic")){
											String respData="";
											if (sysManagerAvailability) {
												    cmdForDsp = "fdd_lte";
													try {
														if (!checkFddOrTdd(packBand, null)
																.get("type").equalsIgnoreCase("FDD")) {
															cmdForDsp = "tdd_lte";
															
																
															boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
															if(!cmdStatus){
																new AutoOperationServer()
																.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable failed:LTE\"}");
																//return false;
															}else{
																fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
																
															}
															
															
														} 
													} catch (Exception e) {
														fileLogger.error("Exception in setSufiConfig of class FourgOperations with message:" + e.getMessage());
													}
													int fddtddRestartCmd=-1;
													if(cmdForDsp.equalsIgnoreCase("fdd_lte"))
													{
														fddtddRestartCmd=4;
													}
													else 
													{
														fddtddRestartCmd=5;
														String testQuery="select  * from gpsdata where logtime > timezone('utc'::text, now() )- INTERVAL '10 sec';";
														 JSONArray jo = operations.getJson(testQuery);
														 fileLogger.debug("@Jo= query 's length TDD = "+jo.length());
														 if(jo==null || jo.length()==0)
														 {
															 new AutoOperationServer()
																.sendText("{\"result\":\"fail\",\"msg\":\"No GPS Found Aborting TDD\"}");
															 fileLogger.debug("@Inside JO after query so aborting tdd. ");
															 continue;
														 
														 }
																				
													}
													
													String SystemManagerToSwitchDSP="";
													JSONArray currentDeviceArray = new Operations().getJson("select * from btsmaster where ip = '" + hm.get("ip") + "' ;");
													SystemManagerToSwitchDSP=currentDeviceArray.getJSONObject(0).getString("systemmanager");
													if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag  && !running_2g && !running_3g  && tech_prev.equalsIgnoreCase(cmdForDsp) && attn_prev==l1_attn) {
														error_flag=false;
														
														
														respData=switchDsp("restart",fddtddRestartCmd,99,999,SystemManagerToSwitchDSP);
													}
													else {
														respData=switchDsp(cmdForDsp,0,0,l1_attn,SystemManagerToSwitchDSP);
													}
													if(respData.equals("")){
														new AutoOperationServer()
														.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:LTE\"}");
														if(TrackingcontinueuponFailure==1) {
																continue;
														}
														else
															return false;
//retrun false;;
													}else{
														fileLogger.debug("@sleep about to sleep in LTE System Manager Switching");
														
														Thread.sleep(sysManWait);
													}
													fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
													updateStatusOfBts4g(hm.get("ip"));
												}
												}
										DBDataService.configurdTxPower=powerLTE;
									    DBDataService.configuredFreq=caulatedFreq;
									    DBDataService.IsRestartRequired= false;
									    
										distance=10;
										//int powerLTE = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
										
										LinkedHashMap<String,String> param=operations.setParamsFor4gConfig(tempJsonObjectFor4g,hm.get("ip"),powerLTE,l1_attn);
										//LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"));
										//LinkedHashMap<String,String> param=null;
										boolean result=false;
										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
											DBDataService.isInterrupted = true;
											// AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
											return false;
//
										}else{
											result = new Common().send4gConfigurationToNode(Integer.parseInt(param.get("adminState")),param);
											fileLogger.debug("@vishal8");	
										}
										if(!result) 
										{
											new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:LTE(EARFCN:"+packEarfcn+",PCI:"+packPci+")\",\"packet\":"+tempJsonObjectFor4g.toString()+"}");
											fileLogger.debug("@vishal9");
											if(TrackingcontinueuponFailure==1) {
												error_flag=true;											
												attn_prev=l1_attn;
												prev_ip= hm.get("ip");
												tech_prev=cmdForDsp;
												continue;
											}
											else
												return false;
//retrun false;;
										}else{
											new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:LTE(EARFCN:"+packEarfcn+",PCI:"+packPci+")\",\"packet\":"+tempJsonObjectFor4g.toString()+"}");
											
										}
										
										
										if(DBDataService.IsRestartRequired)
										{
											
											String SystemManagerToSwitchDSP="";
											JSONArray currentDeviceArray = new Operations().getJson("select * from btsmaster where ip = '" + hm.get("ip") + "' ;");
											SystemManagerToSwitchDSP=currentDeviceArray.getJSONObject(0).getString("systemmanager");
											
											
											String respData=switchDsp("restart",getdspProcessId(cmdForDsp),99,999,SystemManagerToSwitchDSP);
											
											
											if(respData.equals("")){
												new AutoOperationServer()
												.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed(RESET) :LTE\"}");
												//return false;
											}else{
												fileLogger.debug("@sleep about to sleep in LTE System Manager Switching(RESET)");
												
												Thread.sleep(sysManWait);
												fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
												updateStatusOfBts4g(hm.get("ip"));
											}
											
										}
										
										
										param.put("ip",hm.get("ip"));
										operations.setEsufiConfigOnDb(param);
										fileLogger.debug("@vishal4g10");
										fileLogger.debug("@vishal4g@sunil4g10");
									    
									    
										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
											new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
											DBDataService.isInterrupted = true;
											return false;

										}else{
											if(falconType.equalsIgnoreCase("standard")){
												isSucceded = switchAntena(band4g,"4G","8",sectorId);
											}else{
												if(band4g.equals("2300") || band4g.equals("2600")){
													isSucceded = switchAntena(band4g,"4G","8",sectorId);
												}else{
													isSucceded = switchAntena(band4g,"3G","8",sectorId);
												}
											}
											fileLogger.debug("@vishal11");
										}

										if(!isSucceded) 
										{
											if (DBDataService.rotatedMode==2)
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:LTE\"}");
											else
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:LTE\"}");
											fileLogger.debug("@vishal15");	
											if(TrackingcontinueuponFailure==1) {
												continue;
											}
											else
												return false;
//retrun false;;

										}else
										{
											if(!cmdForDsp.equalsIgnoreCase("tdd_lte")) 
											{
												isSucceded = true;
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
													DBDataService.isInterrupted = true;
													//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
													return false;

												}else{
													if(falconType.equalsIgnoreCase("standard")){
														isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
													}else{
														if(band4g.equals("2300") || band4g.equals("2600")){
															isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
														}else{
															isSucceded = switchAntena(band4g+"-NA","3G","7",sectorId);

														}
													}													
												}

												if(!isSucceded){
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"PA Switching failed:LTE\"}");
													if(TrackingcontinueuponFailure==1) {
														
														continue;
													}
													else
														return false;
//retrun false;;
												}else{
													
												}
											}
										}
										fileLogger.debug("@vishal16");
										//unlock the current device
										boolean lockStatus=false; 
										try{
										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
											DBDataService.isInterrupted = true;
											//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
						                    return false;
										}else{
											Thread.sleep(1000);
											lockStatus=fourgOperations.setCellLockUnlockDdp(data);	
										}
										LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
										log.put("IP",hm.get("ip"));
										log.put("Action","UNLOCK");
										log.put("EARFCN",auditEarfcn);
										log.put("PCI",auditPci);
										   if(!lockStatus){
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
												new DeviceStatusServer().sendText("ok");
												log.put("ERROR","Connection Error on Unlocking");
												auditHandler.auditConfigExt(log);
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;											
													attn_prev=l1_attn;
													prev_ip= hm.get("ip");
													tech_prev=cmdForDsp;
													continue;
												}
												else
													return false;
//retrun false;;
										   }else{
											   
											   //new DeviceStatusServer().sendText(hm.get("ip"));
											   updateDeviceStatus("unlock",hm.get("ip"));
											   new DeviceStatusServer().sendText("ok");
												//auditlog start
												auditHandler.auditConfigExt(log);
												//auditlog end
													
												if(cmdForDsp.equalsIgnoreCase("tdd_lte")) 
												{
													isSucceded = true;
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
														//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
								                        return false;
													}else{
														if(falconType.equalsIgnoreCase("standard")){
															isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
														}else{
															if(band4g.equals("2300") || band4g.equals("2600")){
																isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
															}else{
																isSucceded = switchAntena(band4g+"-NA","3G","7",sectorId);
	
															}
														}													
													}
	
													if(!isSucceded){
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"PA Switching failed:LTE\"}");
														if(TrackingcontinueuponFailure==1) {
															
															continue;
														}
														else
															return false;
//retrun false;;
													}else{
														
													}
												}
												
								
										   }
										
										}catch(Exception e){
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
												DBDataService.isInterrupted = true;
												new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
												fileLogger.error("@interrupt 10 interrupted exception occurs");
												return false;
											}else{
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
											}
											if(TrackingcontinueuponFailure==1) {
												error_flag=true;											
												attn_prev=l1_attn;
												prev_ip= hm.get("ip");
												tech_prev=cmdForDsp;
												continue;
											}
											else
												return false;
//retrun false;;
										}
										fileLogger.debug("@vishal17");
									}
									else
									{
										fileLogger.debug("@vishal18");
										fileLogger.debug("dummy packet creation in 4G for synchronization with other technologies");	
									}
								}
								catch(Exception E)
								{
									fileLogger.debug("exception 4g:"+E.getMessage());
									
									fileLogger.debug("@vishal4g19");
									if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
										DBDataService.isInterrupted = true;
										//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
										new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
										fileLogger.error("@interrupt 4g interrupted exception occurs");
										return false;
									}
									if(TrackingcontinueuponFailure==1) {
										
										continue;
									}
									else
										return false;
//retrun false;;
								}
							        
									if(!tempJsonObjectFor4g.has("dummyCheck")) 
									{
								        //Thread.sleep(tempJsonObjectFor3g.getInt("duration")*60*1000);	
										fileLogger.debug("@sleep in 4g cue");
										fileLogger.debug("@Hey NEW TIME HOLD 11 4g :"+((tempJsonObjectFor4g.getLong("duration"))+holdTimeint)*1000);
										Thread.sleep(((tempJsonObjectFor4g.getLong("duration"))+holdTimeint)*1000);

										//Thread.sleep(operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value")*1000);
										data.put("cmdType", "SET_CELL_LOCK");
										data.put("data", "{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\""+lteCellId+"\"}");

										if(cmdForDsp.equalsIgnoreCase("tdd_lte")) 
										{
											boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
											if(!cmdStatus){
												new AutoOperationServer()
												.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable CMD  failed:LTE\"}");
												//return false;
											}else{
												fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
												
											}
										
										}
										
										
										
										try{
											boolean unlockStatus=false;
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
												//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
						                        return false;
											}else{
												unlockStatus=fourgOperations.setCellLockUnlockDdp(data);	
											}
											LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
											log.put("IP",hm.get("ip"));
											log.put("Action","LOCK");
											log.put("EARFCN",auditEarfcn);
											log.put("PCI",auditPci);
											   if(!unlockStatus){
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
													new DeviceStatusServer().sendText("ok");
													log.put("ERROR","Connection Error on Locking");
													auditHandler.auditConfigExt(log);
													if(TrackingcontinueuponFailure==1) {
														error_flag=true;											
														attn_prev=l1_attn;
														prev_ip= hm.get("ip");
														tech_prev=cmdForDsp;
														continue;
													}
													else
														return false;
//retrun false;;

											   }else{
												   //new DeviceStatusServer().sendText(hm.get("ip"));
												   updateDeviceStatus("lock",hm.get("ip"));
												   new DeviceStatusServer().sendText("ok");
													//auditlog start
													auditHandler.auditConfigExt(log);
													//auditlog end
											   }
											}catch(Exception e){
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
													DBDataService.isInterrupted = true;
													//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
													new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
													fileLogger.error("@interrupt 4g next interrupted exception occurs");
													return false;
												}else{
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
												}
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;											
													attn_prev=l1_attn;
													prev_ip= hm.get("ip");
													tech_prev=cmdForDsp;
													continue;
												}
												else
													return false;
//retrun false;;
											}
								        fileLogger.debug("@vishal21");
								}
									if(sysManagerAvailability){
									//switchDsp("neither");
									}
					}
				}
			
			catch(Exception e) 
			{
				   //switchDsp("neither");
				   fileLogger.debug("@vishal23");
				   fileLogger.error("Exception in method trackMobileOnGivenPacketList :"+e.getMessage());
					if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
						DBDataService.isInterrupted = true;
						//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
						new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
						fileLogger.debug("@interrupt 5 interrupted exception occurs");
						return false;
					}
					if(TrackingcontinueuponFailure==1) {
						continue;
					}
					else
						return false;
//retrun false;;
			}				
			}
			iterationCount++;
		}
		//String updateStatusVerdict= updateStatus==true?"SUCCESS":"FAIL";
		//return Response.status(201).entity(updateStatusVerdict).build();
	    new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Done\"}");
		
		fileLogger.debug("@vishal22");
   }catch(Exception E){
	   fileLogger.debug("@vishal24");
	   fileLogger.error("Exception in method trackMobileOnGivenPacketList :"+E.getMessage());
	   //fileLogger.debug("Thread.currentThread().isInterrupted() is:"+Thread.currentThread().isInterrupted());
	   fileLogger.debug("DBDataService.isInterrupted is:"+DBDataService.isInterrupted);
		if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
			DBDataService.isInterrupted = true;
			//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
			new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
			fileLogger.error("@interrupt  6 interrupted exception occurs");
		}
	   return false;
   }finally{
	   fileLogger.debug("@vishal26 calling final");
	 //  if (countl1l2>0) 
	   lockUnlockAllDevices(devicesMapOverTech,1);
	   //new CommonService().updateStatusOfGivenBts("all");
	   //new DeviceStatusServer().sendText("all");
	   new DeviceStatusServer().sendText("ok");
	   fileLogger.debug("@vishal27 calling final done");
   }
   //boolean updateStatus=common.executeDLOperation("update config_status set status=1");
   fileLogger.debug("Exit Function : autoManualTackMobileOnGivenPacketList");
   return true;
   }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   public boolean autoManualTackMobileOnGivenPacketList(String configData,int freq,int antennaId,int trackTime,boolean sysManagerAvailability,boolean switchAvailability,int repititionFreq)
   {
	   
	   fileLogger.debug("Inside Function : autoManualTackMobileOnGivenPacketList");
	   boolean error_exists_tracking= false; 
	   int attn_prev=-1;
	   String tech_prev="";
	   boolean error_flag=false;
	   
	   
	   int holdTime= Integer.parseInt(DBDataService.configParamMap.get("HoldTime"));
	   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTime 1wa =  "+holdTime);
	   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTime 2wa =  "+DBDataService.configParamMap.get("addition_time"));
	   
	   int holdTimeint=holdTime;	
	   if(holdTime!=0)
	   {
		   
		    holdTimeint+=Integer.parseInt(DBDataService.configParamMap.get("addition_time"));
	   }
	   
	   
	   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTimeint =  "+holdTimeint);
	   
	   fileLogger.debug("autoManualTackMobileOnGivenPacketList  holdTimeint =  "+holdTimeint);
	   fileLogger.debug("autoManualTackMobileOnGivenPacketList  trackTime =  "+trackTime);
	   
	   String prev_ip="";
	   boolean running_2g=false;
	   boolean running_3g=false;
	   boolean running_4g=false;
	   int TrackingcontinueuponFailure=Integer.parseInt(DBDataService.configParamMap.get("TrackingcontinueuponFailure"));
	   
	   fileLogger.debug("autoManualTackMobileOnGivenPacketList  TrackingcontinueuponFailure =  "+TrackingcontinueuponFailure);
	   fileLogger.debug("in autoManualTackMobileOnGivenPacketList");
		long sysManWait=4000l;
		HashMap<String,Integer> powerSettingValue= new HashMap<String,Integer>();
		try{
			sysManWait=Long.parseLong(DBDataService.configParamMap.get("sysmanwait"));
			fileLogger.debug("@sysManWait is :"+sysManWait);
		}catch(Exception e){
			fileLogger.error("exception in getting sysManWait message :"+sysManWait);
		}
		String falconType=DBDataService.configParamMap.get("falcontype");
		fileLogger.debug("@track falconType is :"+falconType);
	   int sectorId = new CommonService().getSectorFromAntennaId(antennaId);
	   updateActiveTrackingAntenna(antennaId);
	   //switchAntenna(antennaId); 
	   JSONObject configJson = null;
	   AuditHandler auditHandler = new AuditHandler();
	   Operations operations = new Operations();
	   
	   JSONArray json2gArray=new JSONArray();
	   
	   JSONArray json3gArray=new JSONArray();
	   JSONArray json4gArray=new JSONArray();
	   
	   HashMap<String,ArrayList<HashMap<String,String>>> devicesMapOverTech = operations.getAllBtsInfoByTech();
	   String auditArfcn="";
	   String auditUarfcn="";
	   String auditPsc="";
	   String auditEarfcn="";
	   String auditPci="";
	   String lteCellId="1";
	   
	   try{
			String antennaProfileName = operations.getJson("select profile_name from antenna where id="+antennaId).getJSONObject(0).getString("profile_name");
			new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Started("+antennaProfileName+")\"}");
			new ScanTrackModeServer().sendText("track&Active&"+antennaProfileName);
			configJson = new JSONObject(configData);
		   JSONArray dataArray = configJson.getJSONArray("config_data");
		   
		   for(int i=0;i<dataArray.length();i++)
		   {
			   String tech=dataArray.getJSONObject(i).getString("tech");
			
			if(tech.equalsIgnoreCase("2g")){
				   json2gArray.put(dataArray.getJSONObject(i)); 
			   }else if(tech.equalsIgnoreCase("3g")){
				   json3gArray.put(dataArray.getJSONObject(i));
			   }else if(tech.equalsIgnoreCase("4g")){
				   json4gArray.put(dataArray.getJSONObject(i));
			   }else{
				   JSONArray tempJsonArr=dataArray.getJSONObject(i).getJSONArray("data");
				   for(int j=0;j<tempJsonArr.length();j++){
					 JSONObject tempJsonObj=tempJsonArr.getJSONObject(j); 
					 if(tempJsonObj.getString("flag").equalsIgnoreCase("self")){
						 if(!tempJsonObj.getString("arfcn").equalsIgnoreCase("")){
							 json2gArray.put(dataArray.getJSONObject(i));
							 fileLogger.debug("line 678 :"+json2gArray.length());
						 }else if(!tempJsonObj.getString("uarfcn").equalsIgnoreCase("")){
							 json3gArray.put(dataArray.getJSONObject(i));
							 fileLogger.debug("line 681 :"+json3gArray.length());
						 }else if(!tempJsonObj.getString("earfcn").equalsIgnoreCase("")){
							 json4gArray.put(dataArray.getJSONObject(i));
							 fileLogger.debug("line 684 :"+json4gArray.length());
						 }
					 }
				   }
			   }
		   }
		   
		   fileLogger.debug("2g array size :"+json2gArray.length());
		   fileLogger.debug("3g array size :"+json3gArray.length());
		   fileLogger.debug("4g array size :"+json4gArray.length());

		   ArrayList<JSONObject> json2gList=CommonService.getSortedList(json2gArray);
		   ArrayList<JSONObject> json3gList=CommonService.getSortedList(json3gArray);
		   ArrayList<JSONObject> json4gList=CommonService.getSortedList(json4gArray);
	       
		   int json2gListSize=json2gList.size();
	       int json3gListSize=json3gList.size();
	       int json4gListSize=json4gList.size();
	       
		   //int largeListSize=json2gListSize>=json3gListSize?json2gListSize:json3gListSize;
		   //int largeListSize=json2gListSize>=json3gListSize?(json2gListSize>=json4gListSize?json2gListSize:json4gListSize):(json4gListSize>=json3gListSize?json4gListSize:json3gListSize);
	       
		   ArrayList<JSONObject> newJson2gList=new ArrayList<JSONObject>();
	       ArrayList<JSONObject> newJson3gList=new ArrayList<JSONObject>();
	       ArrayList<JSONObject> newJson4gList=new ArrayList<JSONObject>();
	       
			/* **********************start**************************** */
			LinkedHashMap<String,PacketCount> packetMap = new LinkedHashMap<String,PacketCount>();
			  for(int i=0;i<json2gList.size();i++){
				  if(packetMap.get(json2gList.get(i).getString("plmn"))!=null){
					  PacketCount dt=packetMap.get(json2gList.get(i).getString("plmn"));
					  int count=dt.getCount2g();
					  if(count==0){
						  dt.setInitIndex2g(i);
					  }
					  count++;
					  dt.setCount2g(count);
				  }else{
					  PacketCount dt=new PacketCount(1,i,0,-1,0,-1);
					  packetMap.put(json2gList.get(i).getString("plmn"), dt);
				  }
			  }
			  for(int j=0;j<json3gList.size();j++){
				  if(packetMap.get(json3gList.get(j).getString("plmn"))!=null){
					  PacketCount dt=packetMap.get(json3gList.get(j).getString("plmn"));
					  int count=dt.getCount3g();
					  if(count==0){
						  dt.setInitIndex3g(j);
					  }
					  count++;
					  dt.setCount3g(count);
				  }else{
					  PacketCount dt=new PacketCount(0,-1,1,j,0,-1);
					  packetMap.put(json3gList.get(j).getString("plmn"), dt);
				  }
			  }
			  for(int k=0;k<json4gList.size();k++){
				  if(packetMap.get(json4gList.get(k).getString("plmn"))!=null){
					  PacketCount dt=packetMap.get(json4gList.get(k).getString("plmn"));
					  int count=dt.getCount4g();
					  if(count==0){
						  dt.setInitIndex4g(k);
					  }
					  count++;
					  dt.setCount4g(count);
				  }else{
					  PacketCount dt=new PacketCount(0,-1,0,-1,1,k);
					  packetMap.put(json4gList.get(k).getString("plmn"), dt);
				  }
			  }
			  fileLogger.debug("@Feb23 AutoManual Logs1 packetMap"+packetMap);
			   for(String hset:packetMap.keySet()){
				   PacketCount dt=packetMap.get(hset);
				   int count2g=dt.getCount2g();
				   int count3g=dt.getCount3g();
				   int count4g=dt.getCount4g();
				   
				   if(count2g>count3g){
					   if(count3g>count4g){
						   if(count2g>count4g){
							   //case 1
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count4g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count3g;j++){
								   newJson2gList.add(json2gList.get(i2g));
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson4gList.add(jobj);
								   fileLogger.debug("@Feb23 AutoManual Logs4 inside count3gwa");
								   i2g++;
								   i3g++;
							   }
							   for(int k=j;k<count2g;k++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj2.put("duration",json2gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   fileLogger.debug("@Feb23 AutoManual Logs4 inside count2gwa");
								   i2g++;
							   }
						   }else{
							   //case 2
							   //not applicable
						   }
					   }else{
						   if(count2g>count4g){
							   //case 3
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count3g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count4g;j++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
								   newJson4gList.add(json4gList.get(i4g));
								   i2g++;
								   i4g++;
							   }
							   for(int k=j;k<count2g;k++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj2.put("duration",json2gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   i2g++;
							   }
						   }else{
							   //case 4
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   fileLogger.debug("@Feb23 AutoManual Logs4 inside else case 4 look code");
							   for(i=0;i<count3g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count2g;j++){
								   newJson2gList.add(json2gList.get(i2g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson3gList.add(jobj);
								   newJson4gList.add(json4gList.get(i4g));
								   fileLogger.debug("@Feb23 AutoManual Logs4 inside else case 4 look code 2g");
								   i2g++;
								   i4g++;
							   }
							   for(int k=j;k<count4g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj2.put("duration",json4gList.get(i).getString("duration"));
							       fileLogger.debug("@Feb23 AutoManual Logs4 inside else case 4 look code 4g");
								   newJson3gList.add(jobj2);
								   newJson4gList.add(json4gList.get(i4g));
								   i4g++;
							   }
						   } 
					   }
				   }else{
					   if(count3g>count4g){
						   if(count2g>count4g){
							   //case 5
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count4g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
								   fileLogger.debug("@Feb23 AutoManual Logs4 inside count 3g>count4g and count2g > count4g");
							   }
							   int j=i;
							   for(j=i;j<count2g;j++){
								   newJson2gList.add(json2gList.get(i2g));
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json2gList.get(i).getString("plmn"));
							       jobj.put("duration",json2gList.get(i).getString("duration"));
								   newJson4gList.add(jobj);
								   i2g++;
								   i3g++;
							   }
							   for(int k=j;k<count3g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj2.put("duration",json3gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   i3g++;
							   }
						   }else{
							   //case 6
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count2g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count4g;j++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
								   newJson4gList.add(json4gList.get(i4g));
								   i3g++;
								   i4g++;
							   }
							   for(int k=j;k<count3g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj2.put("duration",json3gList.get(i).getString("duration"));
								   newJson4gList.add(jobj2);
								   i3g++;
							   }
						   }
					   }else{
						   if(count2g>count4g){
							   //case 7
							   //not applicable
						   }else{
							   //case 8
							   int i2g=dt.getInitIndex2g();
							   int i3g=dt.getInitIndex3g();
							   int i4g=dt.getInitIndex4g();
							   int i=0;
							   for(i=0;i<count2g;i++){
								   newJson2gList.add(json2gList.get(i2g++));
								   newJson3gList.add(json3gList.get(i3g++)); 
								   newJson4gList.add(json4gList.get(i4g++));
							   }
							   int j=i;
							   for(j=i;j<count3g;j++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json3gList.get(i).getString("plmn"));
							       jobj.put("duration",json3gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
								   newJson3gList.add(json3gList.get(i3g));
								   newJson4gList.add(json4gList.get(i4g));
								   i3g++;
								   i4g++;
							   }
							   for(int k=j;k<count4g;k++){
							       JSONObject jobj= new JSONObject();
							       jobj.put("dummyCheck","false");
							       jobj.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj.put("duration",json4gList.get(i).getString("duration"));
								   newJson2gList.add(jobj);
							       JSONObject jobj2= new JSONObject();
							       jobj2.put("dummyCheck","false");
							       jobj2.put("plmn",json4gList.get(i).getString("plmn"));
							       jobj2.put("duration",json4gList.get(i).getString("duration"));
								   newJson3gList.add(jobj2);
								   newJson4gList.add(json4gList.get(i4g));
								   i4g++;
							   }
						   } 
					   }
				   }
			   }
			  
			  
			/* **********************end**************************** */
	       
		   JSONArray  newJson2gArray = new JSONArray();
		   JSONArray  newJson3gArray = new JSONArray();
		   JSONArray  newJson4gArray = new JSONArray();
		   fileLogger.debug("@Feb23 AutoManual Logs2");
	       for(int i=0;i<newJson2gList.size();i++){
	    	   newJson2gArray.put(newJson2gList.get(i));
	    	   newJson3gArray.put(newJson3gList.get(i));
	    	   newJson4gArray.put(newJson4gList.get(i));
	       }
		   
		   JSONArray JSONArrayFor2g=newJson2gArray;
		   JSONArray JSONArrayFor3g=newJson3gArray;
		   JSONArray JSONArrayFor4g=newJson4gArray;
		   

			int l1 = newJson2gArray.length();
			int l2 = newJson3gArray.length();
			int l3 = newJson4gArray.length();
			fileLogger.debug("@Feb23 AutoManual Logs3 l1="+l1);
			fileLogger.debug("@Feb23 AutoManual Logs3 l2="+l2);
			fileLogger.debug("@Feb23 AutoManual Logs3 l3="+l3);
			int countl1l2 = l1>l2?(l1>l3?l1:l3):(l3>l2?l3:l2);
			
			new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configuration Packet : GSM "+json2gListSize+" : UMTS "+json3gListSize+" : LTE "+json4gListSize+" \"}");
			fileLogger.debug("Configuration Packet : GSM "+json2gListSize+" : UMTS "+json3gListSize+" : LTE "+json4gListSize);
			int iterationCount = 0;

		    for(int repititionCount=0;repititionCount<repititionFreq;repititionCount++)
		    {
				new CurrentOperation(Thread.currentThread());
				fileLogger.debug("@vishal1"+countl1l2);
				
				OperationCalculations oc = new OperationCalculations();
				
				int distance = new Operations().getJson("select value from system_properties where key='coverage'").getJSONObject(0).getInt("value");
				HashMap device = new HashMap();
				int sleepTimeAfter4gOver=0;
				
				for(int i=0;i<countl1l2;i++)
				{
					
					running_2g=false;
				    running_3g=false;
				    running_4g=false;
					if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
						DBDataService.isInterrupted = true;
                        return false;
					}else{
						if(falconType.equalsIgnoreCase("standard")){
						HashMap<String,String> statusMap=lockUnlockAllDevicesOnAutoTrack(devicesMapOverTech,1);
						if(statusMap.get("status").equals("failure")){
							new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+statusMap.get("ip")+"\"}");
							//new CommonService().updateStatusOfGivenBts("all");
							if(TrackingcontinueuponFailure==1) {
								continue;
							}
							else
								return false;
							
						}else{
							updateDeviceStatus("lock","all");
							//new DeviceStatusServer().sendText("all");
							new DeviceStatusServer().sendText("ok");
						}
						}else{
						//lockUnlockAllDevices(devicesMapOverTech, 1);
							//not req in Octasic system
						}
					}

					fileLogger.debug("@vishal2"+countl1l2);
			
					boolean gone_in_2g_3g_4g=false;
					try
						{
							
					
							
						if(JSONArrayFor2g.length()>0 && devicesMapOverTech.get("2G").size()>0)
						{

					 		ArrayList<HashMap<String, String>> ArrayFor2gdevice=devicesMapOverTech.get("2G");	
							HashMap<String,String> hm=null;
							for(int k=0;k<ArrayFor2gdevice.size();k++) {
								for(int n=0;n<JSONArrayFor2g.length();n++) {
									 JSONObject tempJsonObjectFor2g=new JSONObject();
									try {
										tempJsonObjectFor2g = JSONArrayFor2g.getJSONObject(n);
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
									 sleepTimeAfter4gOver=(tempJsonObjectFor2g.getInt("duration")*60*1000);
									if(device.containsKey("2G-"+n)) {
										
										continue;
									}
								 
									 if(!tempJsonObjectFor2g.has("dummyCheck")) {
										
										 JSONArray data=new JSONArray();
										 gone_in_2g_3g_4g =true;
										 String Band2g="";
										try {
											data = (JSONArrayFor2g.getJSONObject(n).getJSONArray("data"));
											Band2g=(data.getJSONObject(0).getString("band"));
										} catch (Exception e) {
											// TODO Auto-generated catch block
											
										} 
										 String devicebnd=ArrayFor2gdevice.get(k).get("hw");
										 String band2g_=getSupportedBand("2G",Band2g);

										 
										 

										 if (!checkSupportedDevice(devicebnd,band2g_))
											 	continue;
 		
										 device.put("2G-"+n, 1);
										 
										 
										 hm=devicesMapOverTech.get("2G").get(k);

									try
									{	
										fileLogger.debug("@vishal2g:"+tempJsonObjectFor2g.toString());
										if(!tempJsonObjectFor2g.has("dummyCheck")) 
										{
											double caulatedFreq = -1;
											JSONArray compPacketArr=tempJsonObjectFor2g.getJSONArray("data");
											if(freq == -1) 
											{
												int length = compPacketArr.length();
												for(int j=0;j<length;j++)
												{
													JSONObject tempObj = compPacketArr.getJSONObject(j);
													if(tempObj.getString("flag").equalsIgnoreCase("self")) 
													{
														int arfcn = tempObj.getInt("arfcn");
														
														caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(arfcn,"2G");
														fileLogger.debug(arfcn);
														fileLogger.debug("@Calcultated arfcn = "+ arfcn );
													}
												}
											}
											else 
											{
												caulatedFreq = freq;
											}
											String packArfcn="";
											String packBand="";
											String band2g="";
											for(int packSize=0;packSize<compPacketArr.length();packSize++){
											
												JSONObject packObj=compPacketArr.getJSONObject(packSize);
												if(packObj.getString("flag").equals("self")){
													packArfcn=packObj.getString("arfcn");
													packBand=packObj.getString("band");
													
													break;
												}
											}
											
											
											band2g=getSupportedBand("2G",packBand);
											//hm=getSupportedDevice(devicesMapOverTech,band2g);
											
									
											String SystemManagerToSwitchDSP=hm.get("systemmanager");
											if(SystemManagerToSwitchDSP!=null)
											{
												if (SystemManagerToSwitchDSP.equals("")==false ) {
													// if(falconType.equalsIgnoreCase("octasic")){
													String respData="";
													
													if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag) {
														error_flag=false;
														respData= switchDsp("restart",2,99,999,SystemManagerToSwitchDSP);
													}
													else{
														respData= switchDsp("gsm",0,0,999,SystemManagerToSwitchDSP);
													}
														
													if(respData.equals("")){
														new AutoOperationServer()
														.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:GSM\"}");
														if(TrackingcontinueuponFailure==1) {
															break;
														}
														else
															return false;
	
													}else{
														fileLogger.debug("@sleep about to sleep in GSM System Manager Switching");
														
														Thread.sleep(sysManWait);
													}
												}
											}
												
											fileLogger.debug("@vaibhav band2g="+band2g);
											int powerGSM=-999;
											
											int GSUUnit=0;
											
										
											powerSettingValue=getPowerFromPowerSetting("2G",band2g,caulatedFreq);
											powerGSM=powerSettingValue.get("power_setting");
											
											DBDataService.configurdTxPower=powerGSM;
										    DBDataService.configuredFreq=caulatedFreq;
											auditArfcn=packArfcn;

											String result="";
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
												return false;
											}else{
												
												result = operations.locateWithNeighbour(tempJsonObjectFor2g,hm.get("ip"),powerGSM);
											}
											
											if(result == null || result.equalsIgnoreCase("{\"STATUS\":\"3\"}")) 
											{
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:GSM(ARFCN:"+packArfcn+")\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;
													prev_ip= hm.get("ip");

													break;
												}
												else
													return false;
				
											}else{
												new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:GSM(ARFCN:"+packArfcn+")\",\"packet\":"+tempJsonObjectFor2g.toString()+"}");
											}
											fileLogger.debug("@vishal2g1");
											
											operations.createNeighbourServerData(tempJsonObjectFor2g,hm.get("ip"),powerGSM);
											boolean isSucceded = true;
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
						                        return false;
											}else{	
												
													if(SystemManagerToSwitchDSP!=null) {
														if (SystemManagerToSwitchDSP.equals("")==false ) {
														//if(falconType.equalsIgnoreCase("standard")){
															isSucceded = switchAntena(band2g,"2G","8",sectorId);
														}
													}else{
														isSucceded = switchAntena(band2g,"3G","8",sectorId);
													}
											}
											if(!isSucceded) 
											{
												if (DBDataService.rotatedMode==2)
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:GSM\"}");
												else
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:GSM\"}");
												if(TrackingcontinueuponFailure==1) {
													break;
												}
												else
													return false;

											}
											else
											{
												isSucceded = true;
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
													DBDataService.isInterrupted = true;
							                        return false;
												}else{	
														if(SystemManagerToSwitchDSP!=null) {
															if ((SystemManagerToSwitchDSP.equals("")==false )) {
															//if(falconType.equalsIgnoreCase("standard")){
																isSucceded = switchAntena(band2g+"-NA","2G","7",sectorId);
															}
														}
														else{
															isSucceded = switchAntena(band2g+"-NA","3G","7",sectorId);	
														}
												}
												if(!isSucceded){
													new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:UMTS\"}");
													if(TrackingcontinueuponFailure==1) {
														break;
													}
													else
														return false;
				//retrun false;;
												}
											}
											fileLogger.debug("@Print to Check the StatusMAnual Tracking ");
											String statusResponse="";
											if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
												DBDataService.isInterrupted = true;
						                        return false;
											}else{	
												
												statusResponse=new CommonService().setLockUnlock(hm.get("ip"),"2");
											}
											
											LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
											
											log.put("IP",hm.get("ip"));
											log.put("Action","UNLOCK");
											log.put("ARFCN",auditArfcn);
											log.put("TECH",hm.get("typename"));
											
											log.put("Sector",antennaProfileName);
											log.put("ARFCN",auditArfcn);
											
											if(statusResponse==null || statusResponse.equals("")){
												new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
												log.put("ERROR","Connection Error on Unlocking");
												auditHandler.auditConfigExt(log);
												if(TrackingcontinueuponFailure==1) {
													error_flag=true;
													prev_ip= hm.get("ip");
													break;
												}
												else
													return false;

											}else{
												
												updateDeviceStatus("unlock",hm.get("ip"));
												new DeviceStatusServer().sendText("ok");
												auditHandler.auditConfigExt(log);
												
											}
											fileLogger.debug("@vishal2g3");
										}
										else
										{
											fileLogger.debug("dummy packet creation in 2G for synchronization with other technologies");
										}
									}
									catch(Exception E)
									{
										fileLogger.error("@vishal exception in 2g packet thread :"+E.getMessage());
										if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
											DBDataService.isInterrupted = true;
											new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
											fileLogger.error("@interrupt  1 interrupted exception occurs");
											return false;
										}
										if(TrackingcontinueuponFailure==1) {
											
											break;
										}
										else
											return false;
									}
									if(!tempJsonObjectFor2g.has("dummyCheck"))
									{
										break;
									}
								}
								 
									 	
							 }
						}
						
							
					 	
							
							
							
							
							
							
							
							
							
							
							
							
							
							
							
						
						}
						
						fileLogger.debug("@vishal2.5"+JSONArrayFor3g.length()+""+devicesMapOverTech.get("3G").size());
						
						
						
						
						
							if(JSONArrayFor3g.length()>0 && devicesMapOverTech.get("3G").size()>0)
							{
								
								ArrayList<HashMap<String, String>> ArrayFor3gdevice=devicesMapOverTech.get("3G");
								for(int k=0;k<ArrayFor3gdevice.size();k++) {
									 
									 for(int n=0;n<JSONArrayFor3g.length();n++) {
										 
										 
										 if(device.containsKey("3G-"+n))
										 {
											 continue;
										 }
										 JSONObject tempJsonObjectFor3g=JSONArrayFor3g.getJSONObject(n);
									
										
											 	
										
										
										ThreegOperations threegOperations=new ThreegOperations();
										fileLogger.debug("@vishal3");
										HashMap<String,String> hm=devicesMapOverTech.get("3G").get(k);
										
										LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
										fileLogger.debug("@vishal4");
									    data.put("cmdType", "SET_CELL_UNLOCK");
									    data.put("systemCode", hm.get("dcode"));
									    data.put("systemId", hm.get("sytemid"));
									    data.put("systemIP", hm.get("ip"));
									    data.put("data", "{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
									
									    fileLogger.debug("@vishal5");
									    fileLogger.debug("line 879:"+tempJsonObjectFor3g.toString());
									    fileLogger.debug("@vishal6");
									    try{
												fileLogger.debug("@vishal3g:"+tempJsonObjectFor3g.toString());
												if(!tempJsonObjectFor3g.has("dummyCheck"))
												{
													
													device.put("3G-"+n, 1);
													gone_in_2g_3g_4g =true;
													
//													 if(flag_3gincrement_totaltotal2g_3g_4gPackets==0)
//													 {
//														 total2g_3g_4gPackets+=JSONArrayFor3g.length();
//														 flag_3gincrement_totaltotal2g_3g_4gPackets=1;
//													 }
													
													
													String SystemManagerToSwitchDSP=hm.get("systemmanager");
													if(SystemManagerToSwitchDSP!=null){
															if (SystemManagerToSwitchDSP.equals("")==false  ) {
														
															//21 April 21 if(falconType.equalsIgnoreCase("octasic")){
															String respData="";
															
															//JSONArray currentDeviceArray = new Operations().getJson("select * from btsmaster where ip = '" + hm.get("ip") + "' ;");
															//SystemManagerToSwitchDSP=currentDeviceArray.getJSONObject(0).getString("systemmanager");
															
															//if (SystemManagerToSwitchDSP.equals("")==false && SystemManagerToSwitchDSP!=null) {
																
																if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag && !running_2g ) {
																	error_flag=false;
																	respData=switchDsp("restart",3,99,999,SystemManagerToSwitchDSP);	
																}
																else {
																		respData=switchDsp("sufi",0,0,999,SystemManagerToSwitchDSP);
																	}
																if(respData.equals("")){
																	new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:UMTS\"}");
																	if(TrackingcontinueuponFailure==1) {
																		
																		break;
																	}
																	else
																		return false;
																	
		//retrun false;;
																}else{
																	fileLogger.debug("@sleep about to sleep in UMTS System Manager Switching");
																	Thread.sleep(sysManWait);
																}
															
															}	
														}
													
												JSONArray compPacketArr=tempJsonObjectFor3g.getJSONArray("data");
												String packUarfcn="";
												String packPsc="";
												String packBand="";
												for(int packSize=0;packSize<compPacketArr.length();packSize++){
													JSONObject packObj=compPacketArr.getJSONObject(packSize);
													if(packObj.getString("flag").equals("self")){
														packUarfcn=packObj.getString("uarfcn");
														packPsc=packObj.getString("psc");
														packBand=packObj.getString("band");
														break;
													}
												}
												auditUarfcn=packUarfcn;
												auditPsc=packPsc;
												fileLogger.debug("@vishal7");
												double caulatedFreq = 1;
													
													if(freq == -1) 
													{
														JSONArray cellConfigurationArr=tempJsonObjectFor3g.getJSONArray("data");
														
														
														for(int kk=0;kk<cellConfigurationArr.length();kk++){
															JSONObject tempJsonObjectForCell=cellConfigurationArr.getJSONObject(kk);
															if(tempJsonObjectForCell.getString("flag").equalsIgnoreCase("self")){
																
																String newDluarfcn=tempJsonObjectForCell.getString("uarfcn");
																String  newUluarfcn = null;
																int dlUarfcnToUluarfcnFormula=new PossibleConfigurations().getFormulaForTheGivenUarfcn(Integer.parseInt(newDluarfcn));
																if(dlUarfcnToUluarfcnFormula!=0)
																{
																	newUluarfcn=Integer.toString(Integer.parseInt(newDluarfcn)+(dlUarfcnToUluarfcnFormula));
																	caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(Integer.parseInt(newDluarfcn),"3G");
																}	
															}
														}
													}
													
													String band3g="";
	//												switch(packBand){
	//												case "5":
	//													band3g="850";
	//													break;
	//												case "8":
	//													band3g="900";
	//													break;
	//												case "3":
	//													band3g="1800";
	//													break;
	//												case "1":
	//													band3g="2100";
	//												}
													band3g=getSupportedBand("3G",packBand);
													fileLogger.debug("@vaibhav band3g="+band3g);
													
													//@mighty raju
													distance=10;
													//int powerUMTS = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
													//int powerUMTS=Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-3G-"+band3g +"-1"));
													//int powerUMTS=getPowerFromPowerSetting("3G",band3g,caulatedFreq);
													
													powerSettingValue=getPowerFromPowerSetting("3G",band3g,caulatedFreq);
													int powerUMTS=-999;
													powerUMTS=powerSettingValue.get("power_setting");
													
													DBDataService.configurdTxPower=powerUMTS;
												    DBDataService.configuredFreq=caulatedFreq;
													LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"),powerUMTS);
													//LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"));
													//LinkedHashMap<String,String> param=null;
													boolean result=false;
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
								                        return false;
													}else{
														result= new Common().sendConfigurationToNode(Integer.parseInt(param.get("adminState")),param);
													}
													fileLogger.debug("@vishal8");
													if(!result) 
													{
														new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:UMTS(UARFCN:"+packUarfcn+",PSC:"+packPsc+")\",\"packet\":"+tempJsonObjectFor3g.toString()+"}");
														fileLogger.debug("@vishal9");
														if(TrackingcontinueuponFailure==1) {
															error_flag=true;
															prev_ip= hm.get("ip");
															
															break;
														}
														else
															return false;
	//retrun false;;
													}else{
														new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:UMTS(UARFCN:"+packUarfcn+",PSC:"+packPsc+")\",\"packet\":"+tempJsonObjectFor3g.toString()+"}");
													}
													param.put("ip",hm.get("ip"));
													operations.setSufiConfigOnDb(param);
													fileLogger.debug("@vishal10");
													fileLogger.debug("@vishal@sunil10");
													//send antenna over udp
													boolean isSucceded = true;
													
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
								                        return false;
													}else{	
															isSucceded = switchAntena(band3g,"3G","8",sectorId);
													}
													fileLogger.debug("@vishal11");
													if(!isSucceded) 
													{
														if (DBDataService.rotatedMode==2)
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:UMTS\"}");
														else
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:UMTS\"}");
														fileLogger.debug("@vishal15");	
														if(TrackingcontinueuponFailure==1) {
															
															break;
														}
														else
															return false;
	//retrun false;;
													}else
													{
														isSucceded = true;
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
															DBDataService.isInterrupted = true;
									                        return false;
														}else{
																isSucceded = switchAntena(band3g+"-NA","3G","7",sectorId);
														}
														if(!isSucceded){
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:GSM\"}");
															if(TrackingcontinueuponFailure==1) {
																break;
															}
															else
																return false;
	//retrun false;;
														}else{
															
														}
													}
													fileLogger.debug("@vishal16");
													//unlock the current device  
													try{
													boolean lockStatus=false;
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
														DBDataService.isInterrupted = true;
								                        return false;
													}else{	
														lockStatus=threegOperations.setCellLockUnlockDdp(data);
													}
													LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
													log.put("IP",hm.get("ip"));
													log.put("Action","UNLOCK");
													log.put("UARFCN",auditUarfcn);
													log.put("PSC",auditPsc);
													log.put("TECH", hm.get("typename"));
													log.put("Sector",antennaProfileName);
													   if(!lockStatus){
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
															//new DeviceStatusServer().sendText("ok");
															log.put("ERROR","Connection Error on Unlocking");
															auditHandler.auditConfigExt(log);
															if(TrackingcontinueuponFailure==1) {
																error_flag=true;
																prev_ip= hm.get("ip");
																break;
															}
															else
																return false;
	//retrun false;;
													   }else{
														   updateDeviceStatus("unlock",hm.get("ip"));
														   new DeviceStatusServer().sendText("ok");
														  // new DeviceStatusServer().sendText(hm.get("ip"));
															//auditlog start
															auditHandler.auditConfigExt(log);
															//auditlog end
													   }
													
													}catch(Exception e){
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
															DBDataService.isInterrupted = true;
															new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
															fileLogger.error("@interrupt  2 interrupted exception occurs");
															return false;
														}else{
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
														}
														if(TrackingcontinueuponFailure==1) {
															error_flag=true;
															prev_ip= hm.get("ip");
															break;
														}
														else
															return false;
	//retrun false;;
													}
													fileLogger.debug("@vishal17");
												}
												else
												{
													fileLogger.debug("@vishal18");
													fileLogger.debug("dummy packet creation in 3G for synchronization with other technologies");	
												}
											}
											catch(Exception E)
											{
												fileLogger.error("exception:"+E.getMessage());
												
												fileLogger.debug("@vishal19");
												if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
													DBDataService.isInterrupted = true;
													new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
													fileLogger.debug("@interrupt  3 interrupted exception occurs");
													return false;
												}
												
												if(TrackingcontinueuponFailure==1) {
													
													break;
												}
												else
													return false;
	//retrun false;;
											}
										        
												if(!tempJsonObjectFor3g.has("dummyCheck")) 
												{
//													fileLogger.debug("@vishal20");
//											        //Thread.sleep(tempJsonObjectFor3g.getInt("duration")*60*1000);
//											        
//													
//													//Thread.sleep(operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value"));
//													fileLogger.debug("@sleep in 3g manual for :"+tempJsonObjectFor3g.getLong("duration"));
//													fileLogger.debug("@Hey NEW TIME HOLD 2 3g :"+((tempJsonObjectFor3g.getLong("duration"))+holdTimeint)*1000);
//													//Thread.sleep(((tempJsonObjectFor3g.getLong("duration"))+holdTimeint)*1000);
//													data.put("cmdType", "SET_CELL_LOCK");
//													data.put("data", "{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\"0\",\"LAC\":\"0\"}");
//													try{
//														boolean unlockStatus=false;
//														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
//															DBDataService.isInterrupted = true;
//									                        return false;
//														}else{	
//															unlockStatus=threegOperations.setCellLockUnlockDdp(data);
//														}
//														LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
//														log.put("IP",hm.get("ip"));
//														log.put("Action","LOCK");
//														log.put("UARFCN",auditUarfcn);
//														log.put("PSC",auditPsc);
//														   if(!unlockStatus){
//																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
//																//new DeviceStatusServer().sendText("ok");
//																log.put("ERROR","Connection Error on Locking");
//																auditHandler.auditConfigExt(log);
//																if(TrackingcontinueuponFailure==1) {
//																	error_flag=true;
//																	prev_ip= hm.get("ip");
//																	break;
//																}
//																else
//																	return false;
//	//retrun false;;
//														   }else{
//															   updateDeviceStatus("lock",hm.get("ip"));
//															   new DeviceStatusServer().sendText("ok");
//															   //new DeviceStatusServer().sendText(hm.get("ip"));
//																//auditlog start
//																auditHandler.auditConfigExt(log);
//																//auditlog end
//														   }
//														}catch(Exception e){
//															if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
//																DBDataService.isInterrupted = true;
//																//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
//																new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
//																fileLogger.error("@interrupt  4 interrupted exception occurs");
//																return false;
//															}else{
//																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
//															}
//															if(TrackingcontinueuponFailure==1) {
//																error_flag=true;
//																prev_ip= hm.get("ip");
//																break;
//															}
//															else
//																return false;
//	//retrun false;;
//														}
//											        fileLogger.debug("@vishal21");
											}
												if(sysManagerAvailability){
												//switchDsp("neither");
												}
												if(!tempJsonObjectFor3g.has("dummyCheck"))
												{
													break;
												}
									     }
									}
								}	
							fileLogger.debug("@vishal4g length:"+JSONArrayFor4g.length()+":4G NODES:"+devicesMapOverTech.get("4G").size());
							if(JSONArrayFor4g.length()>0 && devicesMapOverTech.get("4G").size()>0)
							{
								
								ArrayList<HashMap<String, String>> ArrayFor4gdevice=devicesMapOverTech.get("4G");
								
								
								for(int k=0;k<ArrayFor4gdevice.size();k++) {
									 
									 for(int n=0;n<JSONArrayFor4g.length();n++) {
											 
										 if(device.containsKey("4G-"+n))
										 {
											 continue;
										 }
										 
										 
										 
										
										FourgOperations fourgOperations=new FourgOperations();
										fileLogger.debug("@vishal4g1");
										HashMap<String,String> hm=devicesMapOverTech.get("4G").get(k);
										//HashMap<String,String> hm1=devicesMapOverTech.get("2G").get(0);
										LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
										fileLogger.debug("@vishal4g2");
										data.put("cmdType", "SET_CELL_UNLOCK");
										data.put("systemCode", hm.get("dcode"));
										data.put("systemId", hm.get("sytemid"));
										data.put("systemIP", hm.get("ip"));
										fileLogger.debug("@vishal4g3");
										JSONObject tempJsonObjectFor4g=JSONArrayFor4g.getJSONObject(n);
										fileLogger.debug("4g packet:"+tempJsonObjectFor4g.toString());
										fileLogger.debug("@vishal4g4");
										String cmdForDsp ="";
										int l1_attn=-999;
										try{
											fileLogger.debug("@vishal4g5:"+tempJsonObjectFor4g.toString());
												if(!tempJsonObjectFor4g.has("dummyCheck"))
												{
													
													gone_in_2g_3g_4g =true;
													device.put("4G-"+n, 1);
													
//													 if(flag_4gincrement_totaltotal2g_3g_4gPackets==0)
//													 {
//														 total2g_3g_4gPackets+=JSONArrayFor4g.length();
//														 flag_4gincrement_totaltotal2g_3g_4gPackets=1;
//													 }
													 
													 
													 
													 
													 
													 
													 
													 
													JSONArray compPacketArr=tempJsonObjectFor4g.getJSONArray("data");
													String packEarfcn="";
													String packPci="";
													String packBand="";
													
													for(int packSize=0;packSize<compPacketArr.length();packSize++){
														JSONObject packObj=compPacketArr.getJSONObject(packSize);
														if(packObj.getString("flag").equals("self")){
															packEarfcn=packObj.getString("earfcn");
															packPci=packObj.getString("pci");
															packBand=packObj.getString("band");
															break;
														}
													}
													data.put("data", "{\"CMD_CODE\":\"SET_CELL_UNLOCK\",\"CELL_ID\":\""+lteCellId+"\"}");
													
													
			//										if(falconType.equalsIgnoreCase("octasic")){
			//											if (sysManagerAvailability) {
			//												 cmdForDsp = "fdd_lte";
			//													try {
			//														if (!checkFddOrTdd(packBand, null)
			//																.get("type").equalsIgnoreCase("FDD")) {
			//															cmdForDsp = "tdd_lte";
			//															
			//															
			//															boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
			//															if(!cmdStatus){
			//																new AutoOperationServer()
			//																.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable CMD  failed:LTE\"}");
			//																//return false;
			//															}else{
			//																fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
			//																
			//															}
			//														} 
			//													} catch (Exception e) {
			//														fileLogger.error("Exception in setSufiConfig of class FourgOperations with message:" + e.getMessage());
			//													}
			//													
			//	
			//													String respData=switchDsp(cmdForDsp,0,0,999);
			//													if(respData.equals("")){
			//														new AutoOperationServer()
			//														.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:LTE\"}");
			//														//return false;
			//													}else{
			//														fileLogger.debug("@sleep about to sleep in LTE System Manager Switching");
			//														Thread.sleep(sysManWait);
			//													}
			//													fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
			//													updateStatusOfBts4g(hm.get("ip"));
			//												}
			//												}
													auditEarfcn=packEarfcn;
													auditPci=packPci;
														fileLogger.debug("@vishal7");
															double caulatedFreq = 1;
														
														if(freq == -1) 
														{
															JSONArray cellConfigurationArr=tempJsonObjectFor4g.getJSONArray("data");
															
															
															for(int kk=0;kk<cellConfigurationArr.length();kk++){
																JSONObject tempJsonObjectForCell=cellConfigurationArr.getJSONObject(kk);
																if(tempJsonObjectForCell.getString("flag").equalsIgnoreCase("self")){
																	
																	String newDlearfcn=tempJsonObjectForCell.getString("earfcn");
																	String  newUlearfcn = null;
																	int dlEarfcnToUlearfcnFormula=new PossibleConfigurations().getFormulaForTheGivenEarfcn(Integer.parseInt(newDlearfcn));
																	if(dlEarfcnToUlearfcnFormula!=0)
																	{
																		newUlearfcn=Integer.toString(Integer.parseInt(newDlearfcn)+(dlEarfcnToUlearfcnFormula));
																		caulatedFreq = new OperationCalculations().calulateFreqFromArfcn(Integer.parseInt(newUlearfcn),"4G");
																	}	
																}
															}
														}
														
														//send antenna over udp
														boolean isSucceded = true;
														String band4g="";
														
			//											switch(packBand){
			//											case "1":
			//												band4g="2100";
			//												break;
			//											case "3":
			//												band4g="1800";
			//												break;
			//											case "5":
			//												band4g="850";
			//												break;
			//											case "8":
			//												band4g="900";
			//												break;
			//											/*case "40":
			//												band4g="2300";
			//											break;
			//											case "41":
			//												band4g="2600";*/
			//											}
														
														band4g=getSupportedBand("4G",packBand);
														fileLogger.debug("@vaibhav band4g="+band4g);
														int powerLTE=-999;
														
														if(falconType.equalsIgnoreCase("standard")){
															powerLTE = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
														}else{
			
															powerSettingValue=getPowerFromPowerSetting("4G",band4g,caulatedFreq);
															powerLTE=powerSettingValue.get("power_setting");
															l1_attn=powerSettingValue.get("l1_att");
															
															//powerLTE = Integer.parseInt(powerSettingMap.get(DBDataService.getSystemId()+"-4G-"+band4g +"-1"));
														}
													
														//if(falconType.equalsIgnoreCase("octasic")){
															String SystemManagerToSwitchDSP=hm.get("systemmanager");
															
															if(SystemManagerToSwitchDSP!=null){
																if (SystemManagerToSwitchDSP.equals("")==false  ) {
																String respData="";
																if (sysManagerAvailability) {
																	    cmdForDsp = "fdd_lte";
																		try {
																			if (!checkFddOrTdd(packBand, null)
																					.get("type").equalsIgnoreCase("FDD")) {
																				cmdForDsp = "tdd_lte";
																				
																					
																				boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
																				if(!cmdStatus){
																					new AutoOperationServer()
																					.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable failed:LTE\"}");
																					//return false;
																				}else{
																					fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
																					
																				}
																				
																				
																			} 
																		} catch (Exception e) {
																			fileLogger.error("Exception in setSufiConfig of class FourgOperations with message:" + e.getMessage());
																		}
																		int fddtddRestartCmd=-1;
																		if(cmdForDsp.equalsIgnoreCase("fdd_lte"))
																		{
																			fddtddRestartCmd=4;
																		}
																		else 
																		{
																			fddtddRestartCmd=5;
																			String testQuery="select  * from gpsdata where logtime > timezone('utc'::text, now() )- INTERVAL '10 sec';";
																			 JSONArray jo = operations.getJson(testQuery);
																			 fileLogger.debug("@Jo= query 's length TDD = "+jo.length());
																			 if(jo==null || jo.length()==0)
																			 {
																				 new AutoOperationServer()
																					.sendText("{\"result\":\"fail\",\"msg\":\"No GPS Found Aborting TDD\"}");
																				 fileLogger.debug("@Inside JO after query so aborting tdd. ");
																				 break;
																			 
																			 }
																									
																		}
	
	
																		
																		
																		
																		
																		
																		
																		
																		
																		
																		
																		
																		
																		
																		if(prev_ip.equalsIgnoreCase(hm.get("ip"))&& error_flag  && !running_2g && !running_3g  && tech_prev.equalsIgnoreCase(cmdForDsp) && attn_prev==l1_attn) {
																			error_flag=false;
																			
																			respData=switchDsp("restart",fddtddRestartCmd,99,999,SystemManagerToSwitchDSP);
																		}
																		else {
																			respData=switchDsp(cmdForDsp,0,0,l1_attn,SystemManagerToSwitchDSP);
																		}
																		if(respData.equals("")){
																			new AutoOperationServer()
																			.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed:LTE\"}");
																			if(TrackingcontinueuponFailure==1) {
																					break;
																			}
																			else
																				return false;

																		}else{
																			fileLogger.debug("@sleep about to sleep in LTE System Manager Switching");
																			
																			Thread.sleep(sysManWait);
																		}
																		fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
																		updateStatusOfBts4g(hm.get("ip"));
																	}
																}
															}
														DBDataService.configurdTxPower=powerLTE;
													    DBDataService.configuredFreq=caulatedFreq;
													    DBDataService.IsRestartRequired= false;
													    
														distance=10;
														//int powerLTE = oc.calulatePower(hm.get("ip"),caulatedFreq,distance);
														
														LinkedHashMap<String,String> param=operations.setParamsFor4gConfig(tempJsonObjectFor4g,hm.get("ip"),powerLTE,l1_attn);
														//LinkedHashMap<String,String> param=operations.setParamsForConfig(tempJsonObjectFor3g,hm.get("ip"));
														//LinkedHashMap<String,String> param=null;
														boolean result=false;
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
															DBDataService.isInterrupted = true;
															// AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
															return false;
			//
														}else{
															result = new Common().send4gConfigurationToNode(Integer.parseInt(param.get("adminState")),param);
															fileLogger.debug("@vishal8");	
														}
														if(!result) 
														{
															new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Configuration failed:LTE(EARFCN:"+packEarfcn+",PCI:"+packPci+")\",\"packet\":"+tempJsonObjectFor4g.toString()+"}");
															fileLogger.debug("@vishal9");
															if(TrackingcontinueuponFailure==1) {
																error_flag=true;											
																attn_prev=l1_attn;
																prev_ip= hm.get("ip");
																tech_prev=cmdForDsp;
																break;
															}
															else
																return false;
			//retrun false;;
														}else{
															new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Configured:LTE(EARFCN:"+packEarfcn+",PCI:"+packPci+")\",\"packet\":"+tempJsonObjectFor4g.toString()+"}");
															
														}
														
														
														if(DBDataService.IsRestartRequired)
														{
															
														
															
															String respData=switchDsp("restart",getdspProcessId(cmdForDsp),99,999,SystemManagerToSwitchDSP);
															
															
															if(respData.equals("")){
																new AutoOperationServer()
																.sendText("{\"result\":\"fail\",\"msg\":\"System Manager Switching failed(RESET) :LTE\"}");
																//return false;
															}else{
																fileLogger.debug("@sleep about to sleep in LTE System Manager Switching(RESET)");
																
																Thread.sleep(sysManWait);
																fileLogger.debug("Getting Status After System Manager Swith in case of 4g");
																updateStatusOfBts4g(hm.get("ip"));
															}
															
														}
														
														
														param.put("ip",hm.get("ip"));
														operations.setEsufiConfigOnDb(param);
														fileLogger.debug("@vishal4g10");
														fileLogger.debug("@vishal4g@sunil4g10");
													    
													    
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
															new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
															DBDataService.isInterrupted = true;
															return false;
			
														}else{
															if(falconType.equalsIgnoreCase("standard")){
																isSucceded = switchAntena(band4g,"4G","8",sectorId);
															}else{
																if(band4g.equals("2300") || band4g.equals("2600")){
																	isSucceded = switchAntena(band4g,"4G","8",sectorId);
																}else{
																	isSucceded = switchAntena(band4g,"3G","8",sectorId);
																}
															}
															fileLogger.debug("@vishal11");
														}
			
														if(!isSucceded) 
														{
															if (DBDataService.rotatedMode==2)
																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"STRU is not Rotating:LTE\"}");
															else
																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Antenna Switching failed:LTE\"}");
															fileLogger.debug("@vishal15");	
															if(TrackingcontinueuponFailure==1) {
																break;
															}
															else
																return false;
			//retrun false;;
			
														}else
														{
															if(!cmdForDsp.equalsIgnoreCase("tdd_lte")) 
															{
																isSucceded = true;
																if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
																	DBDataService.isInterrupted = true;
																	//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
																	return false;
			
																}else{
																	if(falconType.equalsIgnoreCase("standard")){
																		isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																	}else{
																		if(band4g.equals("2300") || band4g.equals("2600")){
																			isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																		}else{
																			isSucceded = switchAntena(band4g+"-NA","3G","7",sectorId);
				
																		}
																	}													
																}
				
																if(!isSucceded){
																	new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"PA Switching failed:LTE\"}");
																	if(TrackingcontinueuponFailure==1) {
																		
																		break;
																	}
																	else
																		return false;
			//retrun false;;
																}else{
																	
																}
															}
														}
														fileLogger.debug("@vishal16");
														//unlock the current device
														boolean lockStatus=false; 
														try{
														if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
															DBDataService.isInterrupted = true;
															//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
										                    return false;
														}else{
															Thread.sleep(1000);
															lockStatus=fourgOperations.setCellLockUnlockDdp(data);	
														}
														LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
														log.put("IP",hm.get("ip"));
														log.put("Action","UNLOCK");
														log.put("EARFCN",auditEarfcn);
														log.put("PCI",auditPci);
														   if(!lockStatus){
																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
																new DeviceStatusServer().sendText("ok");
																log.put("ERROR","Connection Error on Unlocking");
																auditHandler.auditConfigExt(log);
																if(TrackingcontinueuponFailure==1) {
																	error_flag=true;											
																	attn_prev=l1_attn;
																	prev_ip= hm.get("ip");
																	tech_prev=cmdForDsp;
																	break;
																}
																else
																	return false;
			//retrun false;;
														   }else{
															   
															   //new DeviceStatusServer().sendText(hm.get("ip"));
															   updateDeviceStatus("unlock",hm.get("ip"));
															   new DeviceStatusServer().sendText("ok");
																//auditlog start
																auditHandler.auditConfigExt(log);
																//auditlog end
																	
																if(cmdForDsp.equalsIgnoreCase("tdd_lte")) 
																{
																	isSucceded = true;
																	if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
																		DBDataService.isInterrupted = true;
																		//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
												                        return false;
																	}else{
																		if(falconType.equalsIgnoreCase("standard")){
																			isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																		}else{
																			if(band4g.equals("2300") || band4g.equals("2600")){
																				isSucceded = switchAntena(band4g+"-NA","4G","7",sectorId);
																			}else{
																				isSucceded = switchAntena(band4g+"-NA","3G","7",sectorId);
					
																			}
																		}													
																	}
					
																	if(!isSucceded){
																		new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"PA Switching failed:LTE\"}");
																		if(TrackingcontinueuponFailure==1) {
																			
																			break;
																		}
																		else
																			return false;
			//retrun false;;
																	}else{
																		
																	}
																}
																
												
														   }
														
														}catch(Exception e){
															if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
																DBDataService.isInterrupted = true;
																new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
																fileLogger.error("@interrupt 10 interrupted exception occurs");
																return false;
															}else{
																new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Unlocking "+hm.get("ip")+"\"}");
															}
															if(TrackingcontinueuponFailure==1) {
																error_flag=true;											
																attn_prev=l1_attn;
																prev_ip= hm.get("ip");
																tech_prev=cmdForDsp;
																break;
															}
															else
																return false;
			//retrun false;;
														}
														fileLogger.debug("@vishal17");
													}
													else
													{
														fileLogger.debug("@vishal18");
														fileLogger.debug("dummy packet creation in 4G for synchronization with other technologies");	
													}
												}
												catch(Exception E)
												{
													fileLogger.debug("exception 4g:"+E.getMessage());
													
													fileLogger.debug("@vishal4g19");
													if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
														DBDataService.isInterrupted = true;
														//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
														new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
														fileLogger.error("@interrupt 4g interrupted exception occurs");
														return false;
													}
													if(TrackingcontinueuponFailure==1) {
														
														break;
													}
													else
														return false;
			//retrun false;;
												}
											        
													if(!tempJsonObjectFor4g.has("dummyCheck")) 
													{
//												        //Thread.sleep(tempJsonObjectFor3g.getInt("duration")*60*1000);	
//														fileLogger.debug("@sleep in 4g cue");
//														fileLogger.debug("@Hey NEW TIME HOLD 11 4g :"+((tempJsonObjectFor4g.getLong("duration"))+holdTimeint)*1000);
//														//Thread.sleep(((tempJsonObjectFor4g.getLong("duration"))+holdTimeint)*1000);
//			
//														//Thread.sleep(operations.getJson("select value from system_properties where key='tracktime'").getJSONObject(0).getLong("value")*1000);
//														data.put("cmdType", "SET_CELL_LOCK");
//														data.put("data", "{\"CMD_CODE\":\"SET_CELL_LOCK\",\"CELL_ID\":\""+lteCellId+"\"}");
//			
//														if(cmdForDsp.equalsIgnoreCase("tdd_lte")) 
//														{
//															boolean cmdStatus= sendOctasicCmd(7,"Tx Controller","TDD_PA_OFF");
//															if(!cmdStatus){
//																new AutoOperationServer()
//																.sendText("{\"result\":\"fail\",\"msg\":\"PA Disable CMD  failed:LTE\"}");
//																//return false;
//															}else{
//																fileLogger.debug("@sleep about to sleep in SendOctasicCmd TX Controller Switching");
//																
//															}
//														
//														}
//														
//														
//														
//														try{
//															boolean unlockStatus=false;
//															if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted){
//																DBDataService.isInterrupted = true;
//																//new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
//										                        return false;
//															}else{
//																unlockStatus=fourgOperations.setCellLockUnlockDdp(data);	
//															}
//															LinkedHashMap<String,String> log = new LinkedHashMap<String,String>();
//															log.put("IP",hm.get("ip"));
//															log.put("Action","LOCK");
//															log.put("EARFCN",auditEarfcn);
//															log.put("PCI",auditPci);
//															   if(!unlockStatus){
//																	new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
//																	new DeviceStatusServer().sendText("ok");
//																	log.put("ERROR","Connection Error on Locking");
//																	auditHandler.auditConfigExt(log);
//																	if(TrackingcontinueuponFailure==1) {
//																		error_flag=true;											
//																		attn_prev=l1_attn;
//																		prev_ip= hm.get("ip");
//																		tech_prev=cmdForDsp;
//																		break;
//																	}
//																	else
//																		return false;
//			//retrun false;;
//			
//															   }else{
//																   //new DeviceStatusServer().sendText(hm.get("ip"));
//																   updateDeviceStatus("lock",hm.get("ip"));
//																   new DeviceStatusServer().sendText("ok");
//																	//auditlog start
//																	auditHandler.auditConfigExt(log);
//																	//auditlog end
//															   }
//															}catch(Exception e){
//																if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
//																	DBDataService.isInterrupted = true;
//																	//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
//																	new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
//																	fileLogger.error("@interrupt 4g next interrupted exception occurs");
//																	return false;
//																}else{
//																	new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Connection Error on Locking "+hm.get("ip")+"\"}");
//																}
//																if(TrackingcontinueuponFailure==1) {
//																	error_flag=true;											
//																	attn_prev=l1_attn;
//																	prev_ip= hm.get("ip");
//																	tech_prev=cmdForDsp;
//																	break;
//																}
//																else
//																	return false;
//			//retrun false;;
//															}
//												        fileLogger.debug("@vishal21");
												}
													if(sysManagerAvailability){
													//switchDsp("neither");
													}
													if(!tempJsonObjectFor4g.has("dummyCheck")) 
													{
														break;
													}
								}
							}
							//Thread.sleep(((tempJsonObjectFor4g.getLong("duration"))+holdTimeint)*1000);
								if(gone_in_2g_3g_4g==true)
								{
									//Thread.sleep(((60)+holdTimeint)*1000);
									Thread.sleep(sleepTimeAfter4gOver);
									gone_in_2g_3g_4g=false;
									
								}
						}
			//		}
					//while(device.size()!=total2g_3g_4gPackets      ||  (Thread.currentThread().isInterrupted()!=false && DBDataService.isInterrupted!=false));
				}
				catch(Exception e) 
				{
					   //switchDsp("neither");
					   fileLogger.debug("@vishal23");
					   fileLogger.error("Exception in method trackMobileOnGivenPacketList :"+e.getMessage());
						if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  e.getMessage().indexOf("interrupted")!=-1){
							DBDataService.isInterrupted = true;
							//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
							new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
							fileLogger.debug("@interrupt 5 interrupted exception occurs");
							return false;
						}
						if(TrackingcontinueuponFailure==1) {
							continue;
						}
						else
							return false;
	//retrun false;;
				}				
				}
				iterationCount++;
			}
			//String updateStatusVerdict= updateStatus==true?"SUCCESS":"FAIL";
			//return Response.status(201).entity(updateStatusVerdict).build();
		    new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Tracking Done\"}");
			
			fileLogger.debug("@vishal22");
	   }catch(Exception E){
		   fileLogger.debug("@vishal24");
		   fileLogger.error("Exception in method trackMobileOnGivenPacketList :"+E.getMessage());
		   //fileLogger.debug("Thread.currentThread().isInterrupted() is:"+Thread.currentThread().isInterrupted());
		   fileLogger.debug("DBDataService.isInterrupted is:"+DBDataService.isInterrupted);
			if(Thread.currentThread().isInterrupted() || DBDataService.isInterrupted ||  E.getMessage().indexOf("interrupted")!=-1){
				DBDataService.isInterrupted = true;
				//new AutoOperationServer().sendText("{\"result\":\"fail\",\"msg\":\"Interrupted\"}");
				new AutoOperationServer().sendText("{\"result\":\"success\",\"msg\":\"Operation Stopped\"}");
				fileLogger.error("@interrupt  6 interrupted exception occurs");
			}
		   return false;
	   }finally{
		   fileLogger.debug("@vishal26 calling final");
		 //  if (countl1l2>0) 

		   
		   lockUnlockAllDevices(devicesMapOverTech,1);
		      
		   
		   
		   //new CommonService().updateStatusOfGivenBts("all");
		   //new DeviceStatusServer().sendText("all");
		   new DeviceStatusServer().sendText("ok");
		   fileLogger.debug("@vishal27 calling final done");
	   }
	   //boolean updateStatus=common.executeDLOperation("update config_status set status=1");
	   fileLogger.debug("Exit Function : autoManualTackMobileOnGivenPacketList");
	   return true;
		   }
   
   public void updateDeviceStatus(String actionType,String ip){
	   fileLogger.info("Inside Function : updateDeviceStatus");
	   Common common = new Common();
	   if(actionType.equalsIgnoreCase("lock")){
		   if(ip.equalsIgnoreCase("all")){
			   common.executeDLOperation("update btsmaster set status=1 where devicetypeid in (1,6)");
		   }else{
			   common.executeDLOperation("update btsmaster set status=1 where ip='"+ip+"'");
		   }
	   }else{
		   if(ip.equalsIgnoreCase("all")){
			   common.executeDLOperation("update btsmaster set status=0 where devicetypeid in (1,6)"); 
		   }else{
			   common.executeDLOperation("update btsmaster set status=0 where ip='"+ip+"'");
		   }
		   
	   }
	   fileLogger.debug("Exit Function : updateDeviceStatus");
   }
   
   	public String getESufiConfigurationWithDefaultValues() {
   	   fileLogger.debug("Inside Function : getESufiConfigurationWithDefaultValues");
		String defaultSufiConfig = null;
		try {
			String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("WEB-INF"));
			File file = new File(absolutePath + "/resources/config/esuficonfig.json");
			defaultSufiConfig = FileUtils.readFileToString(file);
		} catch (Exception e) {
			fileLogger.error("getSufiConfigurationWithDefaultValues ERROR : " + e.getMessage());
		}
	   	   fileLogger.debug("Exit Function : getESufiConfigurationWithDefaultValues");
		return defaultSufiConfig;
	}
	
		public String getBandDluarfcnMapLTE() {
		fileLogger.debug("inside Function : getBandDluarfcnMapLTE");
		String bandDluarfcnMap = null;
		try {
			String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("WEB-INF"));
			File file = new File(absolutePath + "/resources/config/band_dluarfcn_map_LTE.json");
			bandDluarfcnMap = FileUtils.readFileToString(file);
		} catch (Exception e) {
			fileLogger.debug("bandDluarfcnMap ERROR : " + e.getMessage());
		}
		fileLogger.info("Exit Function : getBandDluarfcnMapLTE");
		return bandDluarfcnMap;
	}
	
		public LinkedHashMap<String,String>checkFddOrTdd(String band,String frequency) 
	{
	    fileLogger.info("inside Function : checkFddOrTdd");
		LinkedHashMap<String,String> returnData = new LinkedHashMap<String,String>();
		
	
		JSONArray ja = new Operations().getJson("select * from band_type_mapping where tech='LTE' and band='"+band+"'");
		try {
			returnData.put("type", ja.getJSONObject(0).getString("type"));
		} catch (Exception e) {
			fileLogger.error("exception in checkFddOrTdd for getting band_type_mapping for fdd and tdd message :"+e.getMessage());
		}
		  fileLogger.info("Exit Function : checkFddOrTdd");
		return returnData;
	}
	
		   public String getSufiConfigurationWithDefaultValues(String type)
		{
			   fileLogger.info("inside Function : getSufiConfigurationWithDefaultValues");
			   String fileName = "suficonfig.json";
		switch(type) 
		{
		case "3g":
		   fileName = "suficonfig.json";
		   break;
		case "4g":
		   fileName = "esuficonfig.json";
		   break;
		
		}
		String defaultSufiConfig=null;
		try
		{
		   String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		   absolutePath = absolutePath.substring(0,absolutePath.lastIndexOf("WEB-INF"));
		   File file = new File(absolutePath	+ "/resources/config/"+fileName);
		   defaultSufiConfig = FileUtils.readFileToString(file);
		}
		catch(Exception e)
		{   
		   System.out.println("getSufiConfigurationWithDefaultValues ERROR : "+e.getMessage());
		}
		 fileLogger.info("Exit Function : getSufiConfigurationWithDefaultValues");
		return defaultSufiConfig;
		}
	   
	   
	     public String getESufiConfigurationWithDefaultValues(String defaultSufiConfig,int type)
	     {
	    	 fileLogger.info("inside Function : getESufiConfigurationWithDefaultValues");
	  	   JSONObject jo = null;
	  	   try
	  	   {	   
	  		   jo = new JSONObject(defaultSufiConfig);
	  		   
	  		  switch(type)
	  		   {
	  		   case 1:
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("SUFI_PARAMS").put("SUFI_OP_MODE", "0");
	  			   //jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("TAC_POOL_START", "3000");
	  			   //jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("TAC_POOL_END", "6000");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("CELL_ID", "1");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("DL_EARFCN", "1575");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("PHY_CELL_ID", "199");
	  			   break;
	  		   case 2:
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("SUFI_PARAMS").put("SUFI_OP_MODE", "1");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("TAC_POOL_START", "6001");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("TAC_POOL_END", "9000");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("CELL_ID", "1");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("DL_EARFCN", "1576");
	  			   jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("PHY_CELL_ID", "299");
	  			   break;
	  		   case 3:
	  		   		jo.getJSONObject("SYS_PARAMS").getJSONObject("SUFI_PARAMS").put("SUFI_OP_MODE", "2");
	  		   		jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("TAC_POOL_START", "9001");
	  				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("TAC_POOL_END", "12001");
	  				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("CELL_ID", "1");
	  				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("DL_EARFCN", "1577");
	  				jo.getJSONObject("SYS_PARAMS").getJSONObject("CELL_INFO").put("PHY_CELL_ID", "399");
	  		   		break;
	  		   		
	  		   }
	  		   
	  	   }
	  	   catch(Exception e)
	  	   {
	  		   
	  		 fileLogger.error ("geteSufiConfigurationWithDefaultValues ERROR : "+e.getMessage());
	  	   }
	  	 fileLogger.info("Exit Function : getESufiConfigurationWithDefaultValues");
	  	   return jo.toString();
	     }
   /*************status update for 4g only*********/
	     public String updateStatusOfBts4g(String ipListString)
	 	{	
	    	 fileLogger.info("Inside Function : updateStatusOfBts4g");
	 		Common co = new Common();
	 		Statement smt = null;
	 		Connection con = co.getDbConnection();
	 		
	 		try
	 		{
	 			smt = con.createStatement();
	 			String query=null;
	 			if(ipListString.equalsIgnoreCase("all")){
	 				query = "select * from view_btsinfo where ip not in('0.0.0.0','1.1.1.1')";
	 			}else{
	 				query = "select * from view_btsinfo where ip in('"+ipListString+"')";
	 			}
	 			statusLogger.debug(query);
	 			ResultSet rs = smt.executeQuery(query);
	 			ArrayList<Thread> threadList = new ArrayList<Thread>();
	 			
	 			while(rs.next())
	 			{
	 				final LinkedHashMap<String,String> param = new  LinkedHashMap<String,String>();
	 				param.put("cmdType", "GET_GET_CURR_STATUS");
	 			    param.put("CMD_CODE", "GET_CURR_STATUS");
	 			    param.put("systemIP", rs.getString("ip"));
	 			    param.put("systemId", rs.getString("sytemid"));
	 			    param.put("systemCode", rs.getString("code"));
	 			    param.put("code",rs.getString("code"));
	 			    param.put("status", rs.getString("statuscode"));
	 			    param.put("adminState", rs.getString("adminstate"));
	 			    param.put("id",rs.getString("b_id"));
	 			    param.put("config",rs.getString("config"));
	 			    param.put("devicetypeid",rs.getString("dcode"));
	 			    param.put("name",rs.getString("dname"));
	 			    param.put("status_name",rs.getString("status"));
	 			   getStatusFor4gDeviceSingle(param);
	 	
	 			}
	 			
	 		
	 		}
	 		catch(Exception E)
	 		{
	 		
	 			
	 		//	statusLogger.debug("*****************************************");
	 			statusLogger.debug("Class = TwogOperations , Method : updateStatusOfAllBts");
	 			statusLogger.error("Erorr During updating the status");
	 			statusLogger.debug(E.getMessage());
	 			
	 			//statusLogger.debug("*****************************************");
	 		}
	 		finally
	 		{
	 			try
	 			{
	 				smt.close();
	 				con.close();
	 			}
	 			catch(Exception E)
	 			{
	 				
	 			}
	 		}
	 		
	    	 fileLogger.info("Exit Function : updateStatusOfBts4g");
	 		return "";
	 	}
	 	
	 	public String getStatusFor4gDeviceSingle(LinkedHashMap<String, String> data) throws Exception {
	    	 fileLogger.info("Inside Function : getStatusFor4gDeviceSingle");
	 		LinkedHashMap<String, String> queryParam = new LinkedHashMap<String, String>();

	 		queryParam.put("cmdType", data.get("CMD_CODE"));
	 		queryParam.put("CMD_TYPE", data.get("CMD_CODE"));
	 		queryParam.put("SYSTEM_CODE", data.get("systemCode"));
	 		queryParam.put("SYSTEM_ID", data.get("systemId"));

	 		data.put("data", "{\"CMD_CODE\":\"GET_CURR_STATUS\"}");
	 		statusLogger.debug(data.get("data"));
	 		// Common.log(data.get("data"));
	 		String url = new Common().getDbCredential().get("4gserviceurl");
	 		Response rs = new ApiCommon().sendRequestToUrl("http://" + data.get("systemIP") + url, queryParam,
	 				data.get("data"));
	 		String resultData = "";
	 		if (rs != null) {

	 			resultData = rs.readEntity(String.class);
	 			new ThreegOperations().updateBtsStatus(resultData, data.get("systemIP"), data.get("id"));
	 		} else {
	 			statusLogger.error("throwing exception as rs = " + rs);
	 			throw new Exception("error while connecting");
	 		}
	 		 fileLogger.info("Exit Function : getStatusFor4gDeviceSingle");
	 		return "ok";
	 	}
	 	public String getSupportedBand(String tech,String packBand){
	 		int packBand1=Integer.parseInt(packBand);
	 		String query="select distinct(c_band) from supported_band where tech='"+tech+"'  and band ="+packBand1;
	 		
			JSONArray aa = new Operations().getJson(query);
			String result="";
			try {
				result = aa.getJSONObject(0).getString("c_band");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				
			}
	 		
	 		return result;
	 	}
	 	
	public  boolean checkSupportedDevice(String hw,String band2g){
	 		HashMap<String,String> hm= null;
	 		boolean findband=false;
 			String bandhmTemp = hw;
 			String[] bandhm=bandhmTemp.split("-"); 	
 			if(bandhm[1].contains(",")){
 				String[] bandList=bandhm[1].split(","); 	
 				for(int j=0;j<bandList.length;j++){
 					if(band2g.equals(bandList[j]))
 		 			{
 		 				return true;
 		 			}
 				}
 			}
 			else{
	 			if(band2g.equals(bandhm[1]))
	 			{
	 				return true;
	 			}
 			}
 			return findband; 
	 }
	 	
	
	 	public  HashMap<String,String> getSupportedDevice(HashMap<String,ArrayList<HashMap<String,String>>> devicesMapOverTech,String band2g){
	 		
	 		
	 		int GSUUnit=0;
	 		//String str="2G-1800";
	 		
	 		//return arrOfStr[1] ;
	 		HashMap<String,String> hm= null;
	 		
	 		while(GSUUnit<devicesMapOverTech.get("2G").size()){
	 			hm=devicesMapOverTech.get("2G").get(GSUUnit);
	 			String bandhmTemp = hm.get("hw");
	 			String[] bandhm=bandhmTemp.split("-"); 	
	 			if(bandhm[1].contains(",")){

	 				String[] bandList=bandhm[1].split(","); 	
	 				for(int j=0;j<bandList.length;j++){
	 					if(band2g.equals(bandList[j]))
	 		 			{
	 		 				return hm;
	 		 			}
	 				}
	 			}
	 			else{
		 			if(band2g.equals(bandhm[1]))
		 			{
		 				return hm;
		 			}
	 			}
	 			GSUUnit++;
	 		}
			//}
	 		
	 		return hm;
	 	}
	 	

	 	
 }



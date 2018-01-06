package com.workoutstudioapp.xadessignercr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Sender {
	public static final String ENDPOINT = "https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1";
	public static void main(String[] args) {
		try {
			String xmlPath = "../test-data/out.xml";
			
			Sender sender = new Sender();
			String username = "cpf-02-0586-0860@stag.comprobanteselectronicos.go.cr";
			String password = "d[-_+1J)mt$%+X$@LsC4";
			//System.out.println("------------------------ send ------------------------");
			sender.send(ENDPOINT, xmlPath, username, password);
			
			// sleep two seconds before we query...
			Thread.currentThread().sleep(2000);
			sender.query(ENDPOINT, xmlPath, username, password);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String getToken(String username, String password) throws Exception {
		String token = "";
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost("https://idp.comprobanteselectronicos.go.cr/auth/realms/rut-stag/protocol/openid-connect/token");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("grant_type", "password"));
		urlParameters.add(new BasicNameValuePair("client_id", "api-stag"));
		urlParameters.add(new BasicNameValuePair("client_secret", ""));
		urlParameters.add(new BasicNameValuePair("scope", ""));
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		
	    request.addHeader("content-type", "application/x-www-form-urlencoded");
	    request.setEntity(new UrlEncodedFormEntity(urlParameters));
	    HttpResponse response = httpClient.execute(request);
	    HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");
	    ObjectMapper objectMapper = new ObjectMapper();
	    Map<String, Object> res = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>(){});
	    token = (String) res.get("access_token");
		return token;
	}
	public void send(String endpoint, String xmlPath, String username, String password) {
		try {			
			XPath xPath = XPathFactory.newInstance().newXPath();
			File file = new File(xmlPath);
			byte[] bytes = FileUtils.readFileToString(file, "UTF-8").getBytes("UTF-8");
			String base64 = Base64.encodeBase64String(bytes);
			ComprobanteElectronico comprobanteElectronico = new ComprobanteElectronico();
			comprobanteElectronico.setComprobanteXml(base64);

			Document xml = XmlHelper.getDocument(xmlPath);
			NodeList nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Clave", xml.getDocumentElement(), XPathConstants.NODESET);
			comprobanteElectronico.setClave(nodes.item(0).getTextContent());
			nodes = (NodeList) xPath.evaluate("/FacturaElectronica/FechaEmision", xml.getDocumentElement(), XPathConstants.NODESET);
			//comprobanteElectronico.setFecha(nodes.item(0).getTextContent());

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			comprobanteElectronico.setFecha(format.format(new Date()));
			
			ObligadoTributario receptor = new ObligadoTributario();
			ObligadoTributario emisor = new ObligadoTributario();
			nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Emisor/Identificacion/Tipo", xml.getDocumentElement(), XPathConstants.NODESET);
			emisor.setTipoIdentificacion(nodes.item(0).getTextContent());
			nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Emisor/Identificacion/Numero", xml.getDocumentElement(), XPathConstants.NODESET);
			emisor.setNumeroIdentificacion(nodes.item(0).getTextContent());
			
			nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Receptor/Identificacion/Tipo", xml.getDocumentElement(), XPathConstants.NODESET);
			receptor.setTipoIdentificacion(nodes.item(0).getTextContent());
			nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Receptor/Identificacion/Numero", xml.getDocumentElement(), XPathConstants.NODESET);
			receptor.setNumeroIdentificacion(nodes.item(0).getTextContent());

			comprobanteElectronico.setReceptor(receptor);
			comprobanteElectronico.setEmisor(emisor);

			String token = getToken(username, password);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(endpoint + "/recepcion");
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(comprobanteElectronico);
			System.out.println(json);
			StringEntity params = new StringEntity(json);
			request.addHeader("content-type", "application/javascript");
			request.addHeader("Authorization", "bearer " + token);
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			//System.out.println("Response code: " + response.getStatusLine().getStatusCode());
			printHeaders(response.getAllHeaders());
			String responseString = EntityUtils.toString(entity, "UTF-8");
			//System.out.println(responseString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void printHeaders(Header[] headers) {
		for (Header header : headers) {
			//System.out.println(header.getName() + ": " + header.getValue());
		}
	}

	public void query(String endpoint, String xmlPath, String username, String password) {
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			Document xml = XmlHelper.getDocument(xmlPath);
			
			NodeList nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Clave", xml.getDocumentElement(), XPathConstants.NODESET);
			String clave = nodes.item(0).getTextContent();

			String url = endpoint + "/recepcion/" + clave;
			String token = getToken(username, password);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			request.addHeader("Authorization", "bearer " + token);

			HttpResponse response = httpClient.execute(request);
			System.out.println("Response code: " + response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println("<responseString>");
			System.out.println(responseString);
			System.out.println("</responseString>");
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>(){});
			String respuestaXML = (String) res.get("respuesta-xml");
			respuestaXML = new String(Base64.decodeBase64(respuestaXML), "UTF-8");
			System.out.println(respuestaXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.taste.elasticsearch_taste;

import java.net.InetSocketAddress;

import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.elasticsearch.common.network.NetworkAddress;

import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHits;



public class TasteEventRestTest extends TastePluginTest {
	public void test_recommended_items_from_user() throws Exception {
		final String index = "index-dblp-lei";
		XContentType type = randomFrom(XContentType.values());
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			   
		        //InetSocketAddress endpoint = randomFrom(cluster().httpAddresses());
			InetSocketAddress[] endpoint = cluster().httpAddresses();
                this.restBaseUrl = "http://" + NetworkAddress.format(endpoint[0]);
		        /*String source = "{\"user\":{\"id\":" + i + "},\"item\":{\"id\":" + j + "},\"value\":" + value + ",\"timestamp\":"
		                        + System.currentTimeMillis() + "}";*/
		               // HttpPost post = new HttpPost(restBaseUrl + "/" + index + "/_taste/event");
		               // HttpPost post = new HttpPost(restBaseUrl + "/_taste/event");
		                HttpGet get=new HttpGet(restBaseUrl + "/"+index+"/_taste/event");
		                //设置post传输的参数
		                //post.addHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
		               // post.setHeader("Accept", "application/json");
		               // StringEntity se=new StringEntity(source,Charset.forName("UTF-8"));
		               // post.setEntity(se);
		                //执行post命令
		                System.out.println("post请求已发送11111111111");
  		                HttpResponse response = httpClient.execute(get);
		                System.out.println("post请求已发送");
		                final String content = response.toString();
		                System.out.println(content);
		               
		                
		                //assertEquals("{\"acknowledged\":true}", content);
		            
		       }
       
   }
}



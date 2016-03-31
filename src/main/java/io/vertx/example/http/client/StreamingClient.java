package io.vertx.example.http.client;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.streams.Pump;
import io.vertx.example.util.Runner;

public class StreamingClient extends AbstractVerticle {

	  // Convenience method so you can run it in your IDE
	  public static void main(String[] args) {
	    Runner.runExample(StreamingClient.class);
	  }

	  public void start() {

//		  HttpClient httpClient = vertx.createHttpClient();
//		  
//		  HttpClientRequest request = httpClient.post("http://localhost:8998", response -> {
//			  System.out.println("Received response with status code " + response.statusCode());
//			});
		  
		  
//	        // Read a file
//	        vertx.fileSystem().readFile("/Users/tgupt24/Documents/servers/vertx/MOCK_DATA.json", result -> {
//	            if (result.succeeded()) {
//	                System.out.println(result.result());
//	            } else {
//	                System.err.println("Oh oh ..." + result.cause());
//	            }
//	        });
		  
		  HttpClientRequest request = vertx.createHttpClient(new HttpClientOptions()).put(8998, "localhost", "", resp -> {
		      System.out.println("Response " + resp.statusCode());
		      System.out.println("Response " + resp.statusMessage());
		    });

		  String filename = "/Users/tgupt24/Documents/servers/vertx/MOCK_DATA.json";
		    FileSystem fs = vertx.fileSystem();

		    fs.props(filename, ares -> {
		      FileProps props = ares.result();
		      System.out.println("props is " + props);
		      long size = props.size();
		      request.headers().set("content-length", String.valueOf(size));
		      fs.open(filename, new OpenOptions(), ares2 -> {
		        AsyncFile file = ares2.result();
		        Pump pump = Pump.pump(file, request);
		        file.endHandler(v -> {
		        	request.end();
		        });
		        pump.start();
		      });
		    });
		  
		  
	  }
	 
	}

package org.apache.nutch.test;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.CrawlDb;
import org.apache.nutch.crawl.Generator;
import org.apache.nutch.crawl.Injector;
import org.apache.nutch.fetcher.Fetcher;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NutchConfiguration;
import org.mortbay.jetty.Server;

public class CrawlerTest extends Configured implements Tool {
	public static final String CRAWL_DB = "crawldb";
	public static final String URLS = "urls";
	public static final String SEGMENT = "segment";
	
	Server server;
	
	Injector injector = new Injector();
	Generator generator = new Generator();
	Fetcher fetcher = new Fetcher();
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(NutchConfiguration.create(), new CrawlerTest(), args);
		System.exit(res);
	}
	
	@Override
	public int run(String[] args) throws Exception {
		//Injector
		injector.run(new String[] {CRAWL_DB, URLS});
		List<String> crawlDb = readCrawldb();
		TestUtils.printCrawlDb(crawlDb);
		
		//Generator
		Path[] generatedSegment = generateFetchlist(2, getConf(), false);
		
		Path fetchlist = new Path(new Path(generatedSegment[0],
		        CrawlDatum.GENERATE_DIR_NAME), "part-00000");

	    ArrayList<URLCrawlDatum> contents = readContents(fetchlist);
	    TestUtils.printGeneratedSegment(contents);
	    
	    //Fetcher
	    fetcher.setConf(getConf());
	    boolean parser = getConf().getBoolean("fetcher.parse", true);
	    System.err.println("fetcher.parse: " + parser);
	    
	    fetcher.fetch(generatedSegment[0], 1);
	    List<FetchedData> fetchedContents = readFetchData(generatedSegment[0]);
	    
		return 0;
	}
	
	private List<String> readCrawldb() throws IOException{
		Configuration conf = getConf();
		FileSystem fs = FileSystem.get(conf);

	    Path dbfile=new Path(CRAWL_DB,CrawlDb.CURRENT_NAME + "/part-00000/data");
	    System.out.println("reading:" + dbfile);
	    @SuppressWarnings("resource")
	    SequenceFile.Reader reader=new SequenceFile.Reader(fs, dbfile, conf);
	    ArrayList<String> read=new ArrayList<String>();
	    
	    READ:
	      do {
	      Text key=new Text();
	      CrawlDatum value=new CrawlDatum();
	      if(!reader.next(key, value)) break READ;
	      read.add(key.toString());
	    } while(true);

	    return read;
	  }
	
	private List<FetchedData> readFetchData(Path generatedSegment) {
		//verify content
	    List<FetchedData> contents = null;
		try {
			Path content=new Path(new Path(generatedSegment, Content.DIR_NAME),"part-00000/data");
			SequenceFile.Reader reader=new SequenceFile.Reader(FileSystem.get(getConf()), content, getConf());
			
			ArrayList<String> handledurls=new ArrayList<String>();
			contents = new ArrayList<FetchedData>();
			READ_CONTENT:
			  do {
			  Text key=new Text();
			  Content value=new Content();
			  FetchedData fd = new FetchedData(key, value);
			  contents.add(fd);
			  if(!reader.next(key, value)) break READ_CONTENT;
			  String contentString=new String(value.getContent());
			  if(contentString.indexOf("Nutch fetcher test page")!=-1) { 
			    handledurls.add(key.toString());
			  }
			} while(true);

			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return contents;
	}

	/**
	   * Read contents of fetchlist.
	   * @param fetchlist  path to Generated fetchlist
	   * @return Generated {@link URLCrawlDatum} objects
	   * @throws IOException
	   */
	  private ArrayList<URLCrawlDatum> readContents(Path fetchlist) throws IOException {
	    // verify results
	    SequenceFile.Reader reader = new SequenceFile.Reader(FileSystem.get(getConf()), fetchlist, getConf());

	    ArrayList<URLCrawlDatum> l = new ArrayList<URLCrawlDatum>();

	    READ: do {
	      Text key = new Text();
	      CrawlDatum value = new CrawlDatum();
	      if (!reader.next(key, value)) {
	        break READ;
	      }
	      l.add(new URLCrawlDatum(key, value));
	    } while (true);
	    
	    reader.close();
	    return l;
	  }
	
	/**
	   * Generate Fetchlist.
	   * @param numResults number of results to generate
	   * @param config Configuration to use
	   * @return path to generated segment
	   * @throws IOException
	   */
	  private Path[] generateFetchlist(int numResults, Configuration config,
	      boolean filter) throws IOException {
	    // generate segment
	    Generator g = new Generator(config);
	    Path[] generatedSegment = g.generate(new Path(CRAWL_DB), new Path(SEGMENT), -1, numResults,
	        Long.MAX_VALUE, filter, false);
	    if (generatedSegment==null) return null;
	    return generatedSegment;
	  }

	
	
}


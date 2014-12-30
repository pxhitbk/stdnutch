package org.apache.nutch.test;

import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;

public class URLCrawlDatum {

    Text url;

    CrawlDatum datum;

    public URLCrawlDatum(Text url, CrawlDatum datum) {
      this.url = url;
      this.datum = datum;
    }
    
    @Override
    public String toString() {
    	return "url: " + url.toString() + "datum: " + datum.toString();
    }
  }

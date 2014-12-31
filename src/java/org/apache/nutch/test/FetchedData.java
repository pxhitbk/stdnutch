package org.apache.nutch.test;

import org.apache.hadoop.io.Text;
import org.apache.nutch.protocol.Content;

public class FetchedData {
	private Text key;
	private Content content;
	
	public FetchedData(Text key, Content content) {
		super();
		this.key = key;
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "Key: " + key != null ? key.toString() : "null" + "/ content: " + content != null ? content.toString() : "null";
	}
}

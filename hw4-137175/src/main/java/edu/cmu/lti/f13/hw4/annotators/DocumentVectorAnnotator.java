package edu.cmu.lti.f13.hw4.annotators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.tokenizer.Token;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.tartarus.snowball.ext.EnglishStemmer;
//import org.apache.lucene.analysis.es.SpanishLightStemmer;
//import org.apache.lucene.analysis.fi.FinnishLightStemmer;
//import org.apache.lucene.analysis.en.*;

import edu.cmu.lti.f13.hw4.VectorSpaceRetrieval;
import edu.cmu.lti.f13.hw4.typesystems.Document;
import edu.cmu.lti.f13.hw4.utils.Utils;


public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	public static Object stopwords;
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}
	
private FSList createTermFreqVector(JCas jcas, Document doc) {
		
		
	    //making preprocessing: trimming, lower, stemming, stopwords 
		
		//whitespaces
		String stripWs = doc.getText().trim().replace("[ ]+", " ");
		
		//lowercase
		String lower;
		lower = stripWs.toLowerCase();
		
		//removing stopWords
		ArrayList<String> wordList = new ArrayList<String>();
		
		for(String s: wordList) {
			if(!stopwords.equals(s))
			//if(!stopwords.contains(s))
				wordList.add(s);
		}
		
		//making the stem
		for(int i=0; i<wordList.size(); i++)
			wordList.set(i, stem(wordList.get(i)));
		
		//counting tokens
		HashMap<String, Integer> tokens = new HashMap<String, Integer>();
		for(String s: wordList )
			if(tokens.containsKey(s))
				tokens.put(s, tokens.get(s) + 1);
			else
				tokens.put(s, 1);		
		
		//transforming  the tokenList
		ArrayList<Token> sw = new ArrayList<Token>();
		Token token = null;
		
		/*for (Iterator<Entry<String, Integer>> iterator = tokens.entrySet().iterator(); iterator
				.hasNext();) {
			java.util.Map.Entry<String, Integer> e = iterator.next();*/
		
		
		//no esta funcionando del todo lanza mensaje de error aun!!

		for(Entry<String, Integer> e: tokens.entrySet()) {
			token = new Token(jcas);
			token.getBegin();
			//token.setText(e.getKey());
			//token.setFrequency(e.getValue());
			sw.add(token);
			
			    
				/*
				token.toString(e.getValue());
				token.setStringValue(null, lower);
				sw.add(token);*/
			
				//cast test
				//((Document) token).setText(e.getKey());
				//((Object) token).setFrequency(e.getValue());
				//sw.add(token);
		}
		//append
		return Utils.fromCollectionToFSList(jcas, sw);
	}
	
	public static HashSet<String> loadStopwords() {
		HashSet<String> res = new HashSet<String>();
		try {
			System.out.println(System.getProperty("user.dir"));
			URL url = VectorSpaceRetrieval.class.getResource("/data/stopwords.txt");	         	 
		
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
		
			while((line = br.readLine()) != null)
				if(!line.startsWith("#"))
					res.add(line);
				br.close();
		
	} catch(Exception e) {
		
		System.err.println("Issue reading stopwords.txt: " +e);
		
		return null;
	}
	
	return res;
}

	private String stem(String s) {
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent(s);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	
	private void printDocument(Document doc) {
		
		System.out.println("Print Document: " + doc.getText());
		
		StringBuffer sb = new StringBuffer();
		doc.getTokenList().prettyPrint(0, 1, sb, true);
		System.out.println(sb.toString());
   }
}


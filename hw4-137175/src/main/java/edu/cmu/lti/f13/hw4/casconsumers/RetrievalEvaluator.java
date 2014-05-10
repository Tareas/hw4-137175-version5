package edu.cmu.lti.f13.hw4.casconsumers;

import edu.cmu.lti.f13.hw4.utils.Utils;
import edu.cmu.lti.f13.hw4.utils.Pair;
import edu.cmu.lti.f13.hw4.typesystems.Document;
import edu.cmu.lti.f13.hw4.typesystems.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;


@SuppressWarnings("unused")
public class RetrievalEvaluator extends CasConsumer_ImplBase {
	
	public ArrayList<Integer> qIdList;
	
	public ArrayList<Integer> relList;

	public ArrayList<HashMap<String, Integer>> vectorList; 
	
	public ArrayList<Integer> rankListManhattan;
	public ArrayList<Integer> rankListCosine;
	
	
	
	public void initialize() throws ResourceInitializationException {
		qIdList = new ArrayList<Integer>();
		relList = new ArrayList<Integer>();
		vectorList = new ArrayList<HashMap<String, Integer>>();
		rankListManhattan = new ArrayList<Integer>();
	}

	/**
	 * TODO :: 
	 * 1. construct the global word dictionary 
	 * 2. keep the word frequency for each sentence
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {
		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
		while (it.hasNext()) {
			Document doc = (Document) it.next();
			//Make sure that your previous annotators have populated this in CAS
			@SuppressWarnings("unused")
			FSList fsTokenList = doc.getTokenList();
			
			qIdList.add(doc.getQueryID());
			relList.add(doc.getRelevanceValue());
			vectorList.add(this.coerceFSListToken(doc.getTokenList()));
			//Do something useful here
		}

	}

	/**
	 * TODO 
	 * 1. Compute Cosine Similarity and rank the retrieved sentences 
	 *
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
		
		super.collectionProcessComplete(arg0);
		System.out.println("RetrievalEvaluator.collectionProcessComplete");
		

                // TODO :: compute the cosine similarity measure
		
		assert(qIdList.size()==vectorList.size() && vectorList.size()==relList.size());
		
		int n = qIdList.size();
		HashMap<String, Integer> actualQuery = null;
		double simi;
		ArrayList<Pair<Integer, Double>> 
			simiList = new ArrayList<Pair<Integer, Double>>();
		
		for(int i=0; i<n; i++) {
			if(relList.get(i)==99) {
				if(simiList.size()>0) {
					rankListManhattan.add(this.getRank(simiList, this.relList));
					simiList.clear();
				}
				actualQuery = vectorList.get(i);
			} else {
				
				simiList.add(new Pair<Integer, Double>(
						i,
						computeInverseManhattanDistance(actualQuery, vectorList.get(i))));
			}
		}
		if(simiList.size()>0) {
			rankListManhattan.add(this.getRank(simiList, this.relList));
			simiList.clear();
		}
		
		
		
	}
	
	// TODO :: compute the cosine similarity measure
	
	/*public doublecosineSimilarityMeasure(Matrix inputDocu, Matrix outputDocu){
		double dotProduct = inputDocu.arrayTimes(outputDocu).norm1();
		double euclidianDist = inputDocu.normF()* outputDocu.normF();
		returndotProduct / euclidianDist;
		}*/
	

	private Double computeInverseManhattanDistance(
			HashMap<String, Integer> actualQuery,
			HashMap<String, Integer> hashMap) {
		// TODO Auto-generated method stub
		return null;
	}

	private Integer getRank(ArrayList<Pair<Integer, Double>> simiList,
			ArrayList<Integer> relList2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @return cosine_similarity
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private double computeCosineSimilarity(
			Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity=0.0;
		
		// TODO :: compute cosine similarity between two sentences
		Map<String, Integer> minMap, otherMap;
		if(queryVector.size() < docVector.size()) {
			minMap = queryVector;
			otherMap = docVector;
		} else {
			minMap = docVector;
			otherMap = queryVector;
		} 
		String s;
		Iterator itMin = minMap.values().iterator(),
				itOther = otherMap.values().iterator();
		double normMin = 0, normOther = 0;
		for(Map.Entry<String, Integer> e: minMap.entrySet()) {
			s = e.getKey();
			if(otherMap.containsKey(s))
				cosine_similarity += e.getValue() * otherMap.get(s);
			normMin += Math.pow((Integer)itMin.next(), 2);
			normOther += Math.pow((Integer)itOther.next(), 2);
		}
		while(itOther.hasNext())
			normOther += Math.pow((Integer)itOther.next(), 2);

		return cosine_similarity/(normMin*normOther);
	}
		
	
	private HashMap<String, Integer> coerceFSListToken(FSList list) {
		ArrayList<Token> tokens = Utils.fromFSListToCollection(list, Token.class);
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		for(Token t: tokens)
			ret.put(t.getText(), t.getFrequency());
		return ret;
	}
}

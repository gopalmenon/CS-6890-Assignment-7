package menon.cs6890.assignment7;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class MainClass {

	final static String OPEN_NL_BIN = "./en-sent.bin";
	public static String robot_inputs = "./robot_inputs.txt";
	public static LexicalizedParser mLexParser = null;
	
	public static void main(String[] args) {
		
		MainClass mainClass = new MainClass();
		mLexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		
		List<String> robotCommands = mainClass.getRobotCommandsFromTextFile(robot_inputs);
		
		if (robotCommands.size() > 0) {
			for (String command : robotCommands) {
				parseSentence(mLexParser, command);
			}
		}
	}
	
	/**
	 * @param inputFilePath
	 * @return the list of words in the text file having one word per line
	 */
	private List<String> getRobotCommandsFromTextFile(String inputFilePath) {
		
		String words = null;
		List<String> returnValue = new ArrayList<String>();
		try {
			BufferedReader textFileReader = new BufferedReader(new FileReader(inputFilePath));
			words = textFileReader.readLine();
			while(words != null) {
				if (words.trim().length() > 0) {
					returnValue.add(words.trim());
				}
				words = textFileReader.readLine();
			}
			textFileReader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + inputFilePath + " was not found.");
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			System.err.println("IOException thrown while reading file " + inputFilePath + ".");
			e.printStackTrace();
			return null;
		}
		
		return returnValue;
	}
	
	/*
	public static void splitAndParseSentences() {
	      InputStream modelIn = null; 
	      SentenceModel model = null;
	      try {
	          modelIn = new FileInputStream(OPEN_NL_BIN);
	          model = new SentenceModel(modelIn);
	          // Here is where we use opennlp.tools.sentdetect.SentenceDetectorME to split text into sentences;          
	          SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
	          String sentences[] = sentenceDetector.sentDetect(route_01);
	          for (int si = 0; si < sentences.length; si++) {
	              System.out.println(sentences[si]);
	              // Parse each sentence with SP
	              parseSentence(mLexParser, sentences[si]);
	          }
	      } catch (InvalidFormatException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	      } catch (IOException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	      }
	  }
	  */
	
	public static void parseSentence(LexicalizedParser lp, String sent) {
        String[] words = sent.split(" ");
        // This option shows parsing a list of correctly tokenized words
        List<CoreLabel> rawWords = Sentence.toCoreLabelList(words);
        //System.out.println("raw words:");
        System.out.println(rawWords);
        //Tree parse = lp.apply(rawWords);
        //System.out.println("pannPrint:");
        //parse.pennPrint();
        //System.out.println();

        // This option shows loading and using an explicit tokenizer
        TokenizerFactory<CoreLabel> tokenizerFactory =
            PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tok =
            tokenizerFactory.getTokenizer(new StringReader(sent));
        List<CoreLabel> rawWords2 = tok.tokenize();
        //System.out.println("rawWords: ");
        //System.out.println(rawWords2);
        Tree parse = lp.apply(rawWords2);

        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        //System.out.println("Dependencies:");
        //System.out.println(tdl);
        //System.out.println();

        // You can also use a TreePrint object to print trees and dependencies
        TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
        tp.printTree(parse);
      }
}

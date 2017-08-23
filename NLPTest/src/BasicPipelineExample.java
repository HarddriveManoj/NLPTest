import java.util.List;
import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentClass;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class BasicPipelineExample {

	// 1. CC Coordinating conjunction
	// 2. CD Cardinal number
	// 3. DT Determiner
	// 4. EX Existential there
	// 5. FW Foreign word
	// 6. IN Preposition or subordinating conjunction
	// 7. JJ Adjective
	// 8. JJR Adjective, comparative
	// 9. JJS Adjective, superlative
	// 10. LS List item marker
	// 11. MD Modal
	// 12. NN Noun, singular or mass
	// 13. NNS Noun, plural
	// 14. NNP Proper noun, singular
	// 15. NNPS Proper noun, plural
	// 16. PDT Predeterminer
	// 17. POS Possessive ending
	// 18. PRP Personal pronoun
	// 19. PRP$ Possessive pronoun
	// 20. RB Adverb
	// 21. RBR Adverb, comparative
	// 22. RBS Adverb, superlative
	// 23. RP Particle
	// 24. SYM Symbol
	// 25. TO to
	// 26. UH Interjection
	// 27. VB Verb, base form
	// 28. VBD Verb, past tense
	// 29. VBG Verb, gerund or present participle
	// 30. VBN Verb, past participle
	// 31. VBP Verb, non-3rd person singular present
	// 32. VBZ Verb, 3rd person singular present
	// 33. WDT Wh-determiner
	// 34. WP Wh-pronoun
	// 35. WP$ Possessive wh-pronoun
	// 36. WRB Wh-adverb

	public static void main(String[] args) {

		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos,truecase, lemma, ner, parse, dcoref, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props,
		// "localhost", 9000, 2);

		// read some text in the text variable
		String text = "you have done a great job";

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
				// this is the Sentiment
				String sentiment = sentence.get(SentimentClass.class);

				System.out.println(String.format("Print: Word: [%s] pos: [%s] ne: [%s] sentiment: [%s]", word, pos, ne,
						sentiment));

			}

		}

		Annotation annotation = pipeline.process(text);
		int longest = 0;
		int mainSentiment = 0;
		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			Tree tree = sentence.get(SentimentAnnotatedTree.class);
			int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
			SimpleMatrix sentiment_new = RNNCoreAnnotations.getPredictions(tree);
			String partText = sentence.toString();
			if (partText.length() > longest) {
				mainSentiment = sentiment;
				longest = partText.length();
			}
			if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
				System.out.println("");
			}
			System.out.println(toCss(mainSentiment));
		}

	}

	private static String toCss(int sentiment) {
		switch (sentiment) {
		case 0:
			return "alert alert-danger";
		case 1:
			return "alert alert-danger";
		case 2:
			return "alert alert-warning";
		case 3:
			return "alert alert-success";
		case 4:
			return "alert alert-success";
		default:
			return "";
		}
	}

}
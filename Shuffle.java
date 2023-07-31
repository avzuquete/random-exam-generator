import java.io.InputStreamReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;
import java.security.NoSuchAlgorithmException;
import com.google.gson.*;

class Shuffle {

static private SecureRandom randGenerator;

static int rand( int max )
{
    byte[] buffer = new byte[4];
    int value;

    if (max == 0) {
        return 0;
    }

    randGenerator.nextBytes( buffer );
    value = ByteBuffer.wrap( buffer ).getInt();
    if (value < 0) value *= -1;
    
    return value % (max + 1);
}

static void logResponse( FileWriter file, int response )
{
    char[] letters = {'a', 'b', 'c', 'd'};

    if (file != null) {
	try {
	file.append( letters[response] );
	} catch (Exception e) {}
    }
}

static public void main( String[] args ) throws NoSuchAlgorithmException
{
    JsonStreamParser in = new JsonStreamParser( new InputStreamReader( System.in ) );
    ArrayList<JsonObject> questions = new ArrayList<JsonObject>();
    int nrQuestions = 0;
    HashSet<String> dejaVu = new HashSet<String>();
    FileWriter responsesFile = null;

    if (args.length != 0) {
	nrQuestions = Integer.valueOf( args[0] );
    }

    if (args.length >= 2) {
	try {
	responsesFile = new FileWriter( args[1], true );
	} catch( Exception e ) {
	    System.err.println( "Problem opening responses file:" + e );
	    System.exit ( -1 );
	}
    }

    randGenerator = SecureRandom.getInstanceStrong();

    // Read all questions and store them FIFO

    while (in.hasNext()) {
	questions.add( in.next().getAsJsonObject() );
    }

    if (nrQuestions == 0) {
        System.out.println( "File has " + questions.size() + " questions" );
	System.exit ( 0 );
    }

    // Get questions by the order they are in the array and process each of them

    for (; questions.size() > 0 && nrQuestions != 0; nrQuestions--) {
        JsonObject q = questions.remove ( rand( questions.size() - 1 ) );
	String type = q.get( "type" ).getAsString();

	// Get a question tag, is any, and if present check wether a question with
	// a similar tag was already used; only use the question otherwise.

	if (q.get( "tag" ) != null) {
	    String tag = q.get( "tag" ).getAsString();

	    if (tag.equals( "" ) == false) {
		if (dejaVu.contains( tag ) == true) {
		    nrQuestions++;
		    continue;
		}

		dejaVu.add( tag );
	    }
	}

	if (type.equals( "1onN" )) {
	    ArrayList<String> wrongAnswers = new ArrayList<String>();
	    ArrayList<String> correctAnswers = new ArrayList<String>();
	    JsonArray trueIdxs = q.get( "correct" ).getAsJsonArray();

	    // Separate correct from wrong questions

	    for (int i = 1;; i++) {
		boolean done = false;

		// Answer i exists in question?

		if (q.has( String.valueOf( i ) ) == false) {
		    break;
		}

		// Check if answer is a correct one

		for (int j = 0; j < trueIdxs.size(); j++) {
		    if (trueIdxs.get( j ).getAsInt() == i) {
			correctAnswers.add( q.get( String.valueOf( i ) ).getAsString() );
			done = true;
			break;
		    }
		}

		if (done == true) continue;

		// Answer is a wrong one

		wrongAnswers.add( q.get( String.valueOf( i ) ).getAsString() );
	    }

            System.out.println( "\\question\n" + q.get( "question" ).getAsString() + "\n\\begin{parts}" );

	    // Select the number of answers according to the value of "choices"

	    int choices = q.get( "choices" ).getAsInt();
	    boolean hasCorrect = false;

	    // First we use a random genarator to pick a correct (if rand=0) and a false otherwise.
	    // 0 or 1 correct answers may be picked.

	    for (int i = 0; i < choices -1; i++) {
		System.out.print( "\\part " );
	        if (rand( choices - 1 ) == 0 && hasCorrect == false) {
		    hasCorrect = true;
		    System.out.println( correctAnswers.get ( rand( correctAnswers.size() - 1 ) ) );
		    logResponse( responsesFile, i );
		}
		else {
		    System.out.println( wrongAnswers.remove ( rand( wrongAnswers.size() - 1 ) ) );
		}
	    }

	    // There's a last question to pick, a correct one (if still missing), otherwise a wrong one.

	    System.out.print( "\\part " );
	    if (hasCorrect == false) {
		System.out.println( correctAnswers.get ( rand( correctAnswers.size() - 1 ) ) );
		logResponse( responsesFile, choices - 1 );
	    }
	    else {
		System.out.println( wrongAnswers.get ( rand( wrongAnswers.size() - 1 ) ) );
	    }

	    System.out.println( "\\end{parts}" );
	}

    }

    if (responsesFile != null) {
	try {
	responsesFile.flush();
	responsesFile.close();
	} catch (Exception e) {}
    }
}

}

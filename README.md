# random-exame-generator
Random exam generator

This generator is a Java program (Shuffle.java) that reads a file with a set of questions and generates a random, LaTeX-formated subset of
questions. It uses the Google JSON handling package.

## Input file format

Each question if the source file is defined by a JSON object, with the following fields (see example below):

* "question" : the text of the question;
* "type" : the type of question (the generator can recognize several types). Currently only 1onN is recognized (means a single correct answer among N);
* "choices" : The value of N in 1onN;
* "tag" : An optional label that is used to avoid selecting very similar questions. Those questions should have an equal tag;
* "correct" : Array with the numbers of the correct answers;
* "1" : Text of answer 1. Add more answers using more sequential numbers.

As you can see in Listing~\ref{q}, LaTeX commands included in the source file need the \ character properly escaped.

```
{
    "question":"This is question number 1, with a \\textbf{bold} word",
    "type":"1onN",
    "choices":4,
    "tag":"first",
    "correct":[3,4],
    "1":"R1 (wrong)",
    "2":"R2 (wrong)",
    "3":"R3 (correct)",
    "4":"R4 (correct)",
    "5":"R5 (wrong)",
    "6":"R6 (wrong)"
}
{
    "question":"This is question number 2, which is similar to number 1",
    "type":"1onN",
    "choices":4,
    "tag":"first",
    "correct":[3,4,8],
    "1":"R1 (wrong)",
    "2":"R2 (wrong)",
    "3":"R3 (correct)",
    "4":"R4 (correct)",
    "5":"R5 (wrong)",
    "6":"R6 (wrong)",
    "7":"R7 (wrong)",
    "8":"R8 (correct)"
}
{
    "question":"This is a completely different question number 3",
    "type":"1onN",
    "choices":4,
    "correct":[1,4],
    "1":"R1 (correct)",
    "2":"R2 (wrong)",
    "3":"R3 (wrong)",
    "4":"R4 (correct)",
    "5":"R5 (wrong)",
    "6":"R6 (wrong)",
    "7":"R7 (wrong)",
    "8":"R8 (wrong)"
}
```

The 1onN format requires at least N-1 wrong answers per question.

## Running the generator

The generator reads a source file from STDIN and writes the random questions to the STDOUT. It takes as arguments the number of random
questions to generate and the name of the file where the solutions are to be saved.

For handling questions in portuguese in a UTF-8 source file you should use the following option with the Java command:

```
-Dfile.encoding=UTF-8
```

# Format of the questions generated

The random questions produced by this program are prepared to be used in LaTeX using the [exam document
class](http://www-math.mit.edu/~psh/exam/examdoc.pdf). Below is an example, produced from the source file presented above:

```
\question
This is a completely different question number 3
\begin{parts}
\part R1 (correct)
\part R5 (wrong)
\part R3 (wrong)
\part R7 (wrong)
\end{parts}

\question
This is question number 1, with a \textbf{bold} word
\begin{parts}
\part R1 (wrong)
\part R5 (wrong)
\part R6 (wrong)
\part R4 (correct)
\end{parts}
```

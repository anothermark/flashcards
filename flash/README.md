READ_ME for Mustard Seed Testing and Card Builder Portals

TABLE OF CONTENTS ----------------------------------------------------------
OVERVIEW - THE BIG PICTURE
MOBILE MUSTARD SEED TEST TAKER
MUSTARD SEED CARD BUILDER
THE MYSQL DATABASE
INSERTING A LIST OF QUESTIONANSWER OBJECTS
MAPPING ARRAYLIST<QUESTIONANSWER> TO THE DB JSON FORMAT, and BACK
MOBILE MUSTARD SEED - FLIPPING THE CARDS
TAKING THE TEST
SEQUENTIAL OR RANDOM LIST ORDER
PRIMARY FILES
LICENSING
TECH STACK



OVERVIEW - THE BIG PICTURE =================================================

The Mobile Mustard Seed knowledge-testing app (index.html) is a web-based 
flashcard/question-answer application built with Spring Boot, hosted on 
Render with an Aiven MySQL database. Files are hosted on GitHub. 
Google AI performed a useful and vital role in the creation of this 
online program. The app is a Maven project built with the 
Eclipse IDE and primarily uses pure Thymeleaf templates to display
in html format Strings 'stored' in QuestionAnswer objects which are 
stored, or referenced, in ArrayLists.
 
It works in tandem with the Card Builder (cardmaker.html) which 
creates, retrieves, updates, deletes and displays questions and answers 'stored'  
as QuestionAnswer objects residing in ArrayLists which are in 
turn converted and stored as Json text 'objects' in a MySql Database. 
This Mustard Seed flashcard test-taker is available in mobile and 
desktop formats, while the Card Builder is only available as a desktop
application. 

MOBILE MUSTARD SEED TEST TAKER ----------------------------------------------

The concept is simple when using the Mustard Seed test taker. A user 
selects a topic which displays a question, and selecting 'Answer' displays the
correct answer. 'Next' brings up the next question. All questions 
can be displayed sequentially, the default, or randomly. Selecting a 
different topic clears the previous fields and begins anew. Or just
click 'Clear All.'

The subject matter of topics can be virtually anything. Answers may be 
simple (2 + 2 = 4) or may contain detailed explanations. This particular 
version tests one's knowledge of scripture verses, and fundamental 
theological and apologetic concepts, mostly in a true/false format. 
Currently only subjects 'Verses[a]' and 'Verses[b]', which are simple 
flipcards, are populated for illustrative purposes. In addition the 
topics Atheism and Trinity currently test one's knowledge at a deeper 
level, in true/false format. Please note that no record is kept of correct 
or incorrect answers. Like traditional hand-held flashcards, repetition 
is the key. 

MUSTARD SEED CARD BUILDER -----------------------------------------------------

The Mustard Seed card builder (cardmaker.html) is similarly straightforward 
and not overly complex. When landing on cardmaker.html no subject 
is selected. Selecting a subject from the far-left buttons column loads 
that particular subject, its name being displayed at the top of 
the Active Subject Workspace. 

If a selected subject is populated with questions and answers they 
will automatically be displayed below in a Live Collection Deck Table. 
If no Questions/Answers exist yet for a particular subject simply leave the ID blank, 
type a question in the second 'Question' text area, and enter the answer 
in the third 'Answer' text area. Click 'Add the Card' to insert that 
Question/Answer object to an ArrayList which will be set in 
the database table. The id, question and answer will appear in the 
below Collection Deck Table. 

To update or delete a Question/Answer simply find the ID in 
the Collection Deck Table, type that number in the first ID 
text area, click 'Find a Card' to bring it up, make your 
changes and select 'Update the Card'. Or, after finding the desired 
card select 'Delete the Card' to remove it from the subject list.


THE MYSQL DATABASE ================================================

INSERTING A LIST OF QUESTIONANSWER OBJECTS --------------------------

When a user clicks 'Add the Card' to populate a subject 
ArrayList<QuestionAnswer> with a QuestionAnswer object the 
CardController.java's @PostMapping("/subject/{id}")'s endpoint method 
is invoked which creates the QuestionAnswer object which is 
passed to the addCard() method of the service (CardServiceImpl.java), 
which in turn gets (or creates) a List<QuestionAnswer> cardList 
from the database. It then inserts the card into this list and returns 
the list to the database by invoking the service's saveToDatabase() method. 

Each row in the MySql database table 'flashcards' has three columns, 
a) id  (bigint), b) questionsandanswers (json arraylist 'object'), and 
c) studysubject (varchar). At its initial inception ten rows were  
created, each with a value of null. This preferred technique 
fixes the primary key for each row without shuffling indexes when 
inserting, deleting, or updating rows, and provides much needed consistency. 
For example, the list for topic 'Atheism' will always reside in the second row, id '2'. 

MAPPING ARRAYLIST<QUESTIONANSWER> TO THE DB JSON FORMAT, and BACK --------------- 

After a QuestionAnswer object is created in CardController.java it is s
added to the list by calling cardService.addCard(id, newItem). In this 
addCard() service method a List<QuestionAnswer> cardList is either created 
(or retrieved from the database). The new QuestionAnswer is added to the 
list and saved to the database using the service's saveToDatabase(subjectId, 
subjectName, cardList) method. And it is inside this saveToDatabase() method where 
the ArrayList<QuestionAnswer> is mapped to a Json String for insertion into the 
database table using Jackson's ObjectMapper: 
String jsonString = objectMapper.writeValueAsString(list); 
 
Finding, updating and deleting QuestionAnswer objects essentially employs 
the same process to retrieve or set the questionsandanswers column of 
the 'flashcards' table. Study the endpoints and methods in the CardController.java
class to track the mapping, etc. and the back-and-forth flow of the list of 
QuestionAnswer objects, and the identifying studysubject column Strings. 

MOBILE MUSTARD SEED - FLIPPING THE CARDS ================================

TAKING THE TEST --------------------------------------------------

When the user (test-taker) first lands on the Mustard Seed 
landing page, index.html, the endpoint @GetMapping("/") 
public String showPage(Model model, HttpSession session) 
method of FlipController.java first clears out stale HttpSession 
parameters (ie., session.removeAttribute("currentQuestionsList"). 
Then it transfers String data from this controller to the front-end 
UI template Thymeleaf; information displayed in the browser interface such as 
model.addAttribute("myTextValue", "Please select a topic to begin."). 
The default value of various flags are also set here.

When the user selects a topic from the drop-down combo-box menu in 
index.html, the FlipController fires @PostMapping("/select-topic") 
public String selectTopic(@RequestParam("topic") String topic, 
HttpSession session, Model model). This method retrieves a 
List<QuestionAnswer> questions from the database as explained 
earlier. This Java list is set in the session, 
session.setAttribute("currentQuestionsList", questions), 
along with other attributes, and persists "until the user 
closes the bowser, logs out, or the session times out." (Google)

And it is here in selectTopic() that the drop-down is activated, 
the first question is retrieved from the session list and 
set in the Model as an attribute to be returned via 
Thymeleaf to the Question textarea of index.html.

The controller's PostMapping for getting the answer is 
@PostMapping("/fetch-answer") public String 
getAnswerForCurrentQuestion(HttpSession session, Model model). 
Here, depending on whether the active deck is shuffled, 
the answer is retrieved from that deck list and added to 
the model as an attribute to be displayed in the GUI: 
QuestionAnswer currentCard = activeDeck.get(currentIndex);
model.addAttribute("myTextValue", currentCard.getQuestion());
model.addAttribute("myTextAnswer", currentCard.getAnswer());
	
Clicking 'Next Question' follows the same logic and is self-explanatory. 
It is important to understand how the index order is calculated: 
currentIndex = (currentIndex + 1) % activeDeck.size(). 

SEQUENTIAL OR RANDOM LIST ORDER -----------------------------

Lastly is the option to cycle through a sequential list or 
randomize it. The logic for this process is found in the FlipController.java's @PostMapping("/toggle-shuffle")
public String toggleShuffle(HttpSession session, Model model) endpoint. 
Selecting 'Switch to Random' returns the current list from the session: 
List<QuestionAnswer> baseList = (List<QuestionAnswer>) 
session.getAttribute("currentQuestionsList");
Here the baseList is cloned, then shuffled, and then set as a session attribute:
List<QuestionAnswer> randomizedDeck = new ArrayList<>(baseList);
Collections.shuffle(randomizedDeck);
session.setAttribute("shuffledQuestionsList", randomizedDeck);
activeWorkingList = randomizedDeck;	


PRIMARY FILES =================================================

index.html  
style2.css
cardmaker.html
style.css
QuestionAnswer.java
CardController.java
FlipController.java
CardService.java
CardServiceImpl.java

LICENSING ======================================================

Pending 
 
TECH STACK =====================================================

Eclipse IDE
Maven
Spring Boot
Spring Data JPA 
Spring Web
Thymeleaf - a "... modern, server-side Java template engine used to dynamically generate HTML pages". 
AI assistance (Google AI)
GitHub - "... a cloud-based web platform that lets software developers store, manage, and collaborate
	on their code."
Aiven - MySql database host - "... an open-scource cloud database platform."
Render - "... a cloud platform as a Service (PaaS) designed to help developers deploy, run, and scale web 
	applications, APIs, databases, and AI agents without managing underlying server infrastructure."
Java
Html5
Cascading Style Sheets - CSS 















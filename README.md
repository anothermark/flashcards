# Mustard Seed Testing and Card Builder Portals

## Table of Contents
- [Overview - The Big Picture](#overview---the-big-picture)
- [Mobile Mustard Seed Test Taker](#mobile-mustard-seed-test-taker)
- [Mustard Seed Card Builder](#mustard-seed-card-builder)
- [The MySQL Database](#the-mysql-database)
  - [Inserting a List of QuestionAnswer Objects](#inserting-a-list-of-questionanswer-objects)
  - [Mapping ArrayList\<QuestionAnswer\> to the DB JSON Format, and Back](#mapping-arraylistquestionanswer-to-the-db-json-format-and-back)
- [Mobile Mustard Seed - Flipping the Cards](#mobile-mustard-seed---flipping-the-cards)
  - [Taking the Test](#taking-the-test)
  - [Sequential or Random List Order](#sequential-or-random-list-order)
- [Primary Files](#primary-files)
- [Licensing](#licensing)
- [Tech Stack](#tech-stack)

---

## Overview - The Big Picture

The **Mobile Mustard Seed** knowledge-testing app (`index.html`) is a web-based flashcard/question-answer application built with **Spring Boot**, hosted on **Render** with an **Aiven MySQL** database. Files are hosted on **GitHub**. Google AI performed a useful and vital role in the creation of this online program. 

The app is a **Maven** project built with the **Eclipse IDE** and primarily uses pure **Thymeleaf** templates to display in HTML format Strings "stored" in `QuestionAnswer` objects which are stored, or referenced, in `ArrayLists`.
 
It works in tandem with the **Card Builder** (`cardmaker.html`) which creates, retrieves, updates, deletes and displays questions and answers stored as `QuestionAnswer` objects residing in `ArrayLists`. These are in turn converted and stored as JSON text "objects" in a MySQL Database. This Mustard Seed flashcard test-taker is available in mobile and desktop formats, while the Card Builder is only available as a desktop application.

---

## Mobile Mustard Seed Test Taker

The concept is simple when using the Mustard Seed test taker:
1. A user selects a topic which displays a question.
2. Selecting **Answer** displays the correct answer.
3. Selecting **Next** brings up the next question. 

All questions can be displayed sequentially (the default) or randomly. Selecting a different topic clears the previous fields and begins anew. Alternatively, users can click **Clear All**.

The subject matter of topics can be virtually anything. Answers may be simple (e.g., `2 + 2 = 4`) or may contain detailed explanations. This particular version tests one's knowledge of scripture verses, and fundamental theological and apologetic concepts, mostly in a true/false format. 

### Current Coverage
* **Verses[a] & Verses[b]:** Simple flipcards populated for illustrative purposes.
* **Atheism & Trinity:** Topics that test knowledge at a deeper level in a true/false format.

*Note: No record is kept of correct or incorrect answers. Like traditional hand-held flashcards, repetition is the key.*

---

## Mustard Seed Card Builder

The Mustard Seed card builder (`cardmaker.html`) is straightforward and streamlined. When landing on the page, no subject is initially selected. Selecting a subject from the far-left buttons column loads that particular subject, displaying its name at the top of the **Active Subject Workspace**.

### Adding a Card
If a selected subject is populated with questions and answers, they automatically display below in a **Live Collection Deck Table**. If no questions or answers exist yet:
1. Leave the **ID** field blank.
2. Type a question in the **Question** text area.
3. Enter the answer in the **Answer** text area.
4. Click **Add the Card** to insert that `QuestionAnswer` object into an `ArrayList` to be set in the database table. The ID, question, and answer will instantly appear in the table below.

### Updating or Deleting a Card
* **To Update:** Find the ID in the table, type that number into the first ID text area, click **Find a Card** to fetch it, make your changes, and select **Update the Card**.
* **To Delete:** After finding the desired card via its ID, select **Delete the Card** to remove it from the subject list.

---

## The MySQL Database

### Inserting a List of QuestionAnswer Objects

When a user clicks **Add the Card**, the following operational pipeline occurs:
1. `CardController.java`'s `@PostMapping("/subject/{id}")` endpoint method is invoked.
2. The `QuestionAnswer` object is created and passed to the `addCard()` method of the service (`CardServiceImpl.java`).
3. The service retrieves (or creates) a `List<QuestionAnswer> cardList` from the database.
4. It inserts the card into this list and saves it back to the database by invoking the service's `saveToDatabase()` method.

Each row in the MySQL database table `flashcards` features three columns:
- **id** (`bigint`)
- **questionsandanswers** (`json` arraylist object)
- **studysubject** (`varchar`)

At its initial inception, ten rows were created with a value of `null`. This technique fixes the primary key for each row without shuffling indexes when inserting, deleting, or updating rows, providing system consistency. For example, the list for the topic **Atheism** will always reside in the second row (`id: 2`).

### Mapping ArrayList\<QuestionAnswer\> to the DB JSON Format, and Back

After a `QuestionAnswer` object is created in `CardController.java`, it is added to the list by calling `cardService.addCard(id, newItem)`. 

Inside the `saveToDatabase(subjectId, subjectName, cardList)` method, the `ArrayList<QuestionAnswer>` is mapped to a JSON String for database insertion using **Jackson's ObjectMapper**:

```java
String jsonString = objectMapper.writeValueAsString(list);
```
 
Finding, updating, and deleting `QuestionAnswer` objects employs this same basic process to retrieve or set the `questionsandanswers` column of the `flashcards` table. Study the endpoints and methods in the `CardController.java` class to track the mapping pipeline and back-and-forth flow.

---

## Mobile Mustard Seed - Flipping the Cards

### Taking the Test

When the test-taker lands on the landing page (`index.html`), the endpoint `@GetMapping("/")` method of `FlipController.java` triggers:
1. It clears out stale `HttpSession` parameters:
   ```java
   session.removeAttribute("currentQuestionsList");
   ```
2. It transfers data from the controller to the front-end Thymeleaf template:
   ```java
   model.addAttribute("myTextValue", "Please select a topic to begin.");
   ```
3. Default structural flags are initialized.

When a user selects a topic from the drop-down combo-box menu, `FlipController` fires:
```java
@PostMapping("/select-topic")
public String selectTopic(@RequestParam("topic") String topic, HttpSession session, Model model)
```

This method retrieves a `List<QuestionAnswer> questions` from the database. This Java list is set in the session and persists until the user closes the browser, logs out, or the session times out:
```java
session.setAttribute("currentQuestionsList", questions);
```

Within `selectTopic()`, the drop-down is activated, and the first question is retrieved from the session list and set in the `Model` as an attribute to be returned via Thymeleaf to the Question textarea of `index.html`.

### Fetching Answers

The controller's `PostMapping` for getting the answer is:
```java
@PostMapping("/fetch-answer")
public String getAnswerForCurrentQuestion(HttpSession session, Model model)
```

Depending on whether the active deck is shuffled, the answer is retrieved from that deck list and added to the model to be displayed in the UI:
```java
QuestionAnswer currentCard = activeDeck.get(currentIndex);
model.addAttribute("myTextValue", currentCard.getQuestion());
model.addAttribute("myTextAnswer", currentCard.getAnswer());
```

Clicking **Next** handles the list traversal logic.### Taking the Test

When the user (test-taker) first lands on the Mustard Seed landing page, `index.html`, the endpoint `@GetMapping("/")` method of `FlipController.java` triggers:
1. It clears out stale `HttpSession` parameters:
   ```java
   session.removeAttribute("currentQuestionsList");
   ```
2. It transfers data from the controller to the front-end Thymeleaf template:
   ```java
   model.addAttribute("myTextValue", "Please select a topic to begin.");
   ```
3. Default status flags are initialized.

When a user selects a topic from the drop-down combo-box menu, `FlipController` fires:
```java
@PostMapping("/select-topic")
public String selectTopic(@RequestParam("topic") String topic, HttpSession session, Model model)
```

This method retrieves a `List<QuestionAnswer> questions` from the database. This Java list is set in the session and persists until the user closes the browser, logs out, or the session times out:
```java
session.setAttribute("currentQuestionsList", questions);
```

Within `selectTopic()`, the drop-down is activated, and the first question is retrieved from the session list and set in the `Model` as an attribute to be returned via Thymeleaf to the Question textarea of `index.html`.

### Fetching Answers

The controller's `PostMapping` for getting the answer is:
```java
@PostMapping("/fetch-answer")
public String getAnswerForCurrentQuestion(HttpSession session, Model model)
```

Depending on whether the active deck is shuffled, the answer is retrieved from that deck list and added to the model to be displayed in the UI:
```java
QuestionAnswer currentCard = activeDeck.get(currentIndex);
model.addAttribute("myTextValue", currentCard.getQuestion());
model.addAttribute("myTextAnswer", currentCard.getAnswer());
```

Clicking **Next Question** follows this same processing logic. The active deck list traversal index order is calculated using a modulo wrap-around loop: 
```java
currentIndex = (currentIndex + 1) % activeDeck.size();
```

---

## Sequential or Random List Order

Users have the option to cycle through questions sequentially or randomize the deck order. This routing configuration is handled inside the `FlipController.java` endpoint:
```java
@PostMapping("/toggle-shuffle")
public String toggleShuffle(HttpSession session, Model model)
```

Selecting **Switch to Random** pulls the core collection back out of the session pipeline:
```java
List<QuestionAnswer> baseList = (List<QuestionAnswer>) session.getAttribute("currentQuestionsList");
```

The `baseList` is cloned to preserve the source array data, randomized, and committed back to the session context as the active working collection:
```java
List<QuestionAnswer> randomizedDeck = new ArrayList<>(baseList);
Collections.shuffle(randomizedDeck);
session.setAttribute("shuffledQuestionsList", randomizedDeck);
activeWorkingList = randomizedDeck; 
```

---

## Primary Files

* **Frontend Layouts:**
  * `index.html` — Core web portal dashboard interface for mobile and desktop test-takers.
  * `cardmaker.html` — Desktop management dashboard panel interface for creating and modifying cards.
* **Style Sheets:**
  * `style.css` — Presentation layout styling attributes for the card creator workspace.
  * `style2.css` — Presentation layout styling attributes for the user portal interface.
* **Core Application Components:**
  * `QuestionAnswer.java` — Plain Old Java Object (POJO) data model defining question and answer properties.
  * `CardController.java` — REST controller handling creation, modification, and deck pruning updates.
  * `FlipController.java` — App runtime controller tracking user sessions, list shuffling, and index loops.
  * `CardService.java` — Business core logic service interface layers.
  * `CardServiceImpl.java` — Implementation class containing database translation and JSON mapping code blocks.

---

## Licensing

* Pending

---

## Tech Stack

* **Development Environment & Tooling:**
  * **Eclipse IDE** — Primary workbench workspace platform used to construct the Java source code.
  * **Maven** — Project dependency control engine managing libraries, builds, and compilation steps.
* **Backend Frameworks:**
  * **Spring Boot** — Core backend engine running structural microservices and routing logic layers.
  * **Spring Data JPA** — Database abstraction engine managing automated entities and connections.
  * **Spring Web** — Web routing controller layer managing client-server HTTP data pipelines.
* **View Layer Engines:**
  * **Thymeleaf** — Modern server-side Java template engine used to dynamically generate responsive HTML pages.
  * **HTML5** — Document object design scaffolding defining interactive forms and views.
  * **CSS3** — Custom layout style components providing structure and UI aesthetics.
* **External Integrations:**
  * **Google AI** — Performed a useful and vital role assisting with programmatic setup and architecture mapping.
  * **GitHub** — Cloud-based source platform letting developers safely version, back up, and store production files.
* **Cloud Infrastructure Services:**
  * **Aiven** — Fully-managed open-source cloud database host provisioning the underlying MySQL storage instance.
  * **Render** — Platform as a Service (PaaS) engine used to deploy, execute, and automatically serve web applications without managing servers.
  * **Java Language Runtime** — Core objective programming runtime environment powering the backend application tier.

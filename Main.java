import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
public class Main {
    public static LinkedList<Integer> storyGraph[]; // declare the graph structure.
    public final static int TOTAL_NODES = 9; // Declare constant total nodes
    public final static int MAX_INVENTORY_SPACE = 3; // The max inventory space.
    public final static int LOCATION_INTEGER = 0; // It is constant because of the command system.
    public static int currentNode = 0; // The current node
    public static String nodeChoice; // Stores a string that decides which node gets to be chosen next.
    public static boolean[] visited = new boolean[TOTAL_NODES]; // create a visited array
    public static final String[][] locationList = { //store the locations and the items inside each area.
            {"house", "cabinet", "desk"},
            {"gasstation", "shelves", "backroom"}
    };
    public static final String[][] questionList = { // question list
            {"How Do You Spell Cat?", "cat", "8"}, // first index: question, second index: answer, third index: IQ points gained if correct.
            {"What is 3x4-1?", "11", "10"},
            {"How Do You Spell USB?", "usb", "9"},
            {"Solve for X: 3x = 12", "4", "14"},
            {"What Is The Opposite of Down", "up", "15"},
            {"What is the square root of 4", "2", "20"},
            {"What is 10+10?", "20", "50"},
            {"Spell dog", "dog", "20"},
            {"What is 3*2/3", "2", "30"}
    };
    public static final String[][] playerInventory = new String[TOTAL_NODES][MAX_INVENTORY_SPACE]; // 3D array to store inventory so players can hop back in time to different nodes.
    public static String[][][] itemList = {
            // Make a 3 dimensional array, the second dimension to store the items based on the location, and the third one to store the items.
            {
                    {"cabinet", "gun", "knife"},
                    {"desk", "smartphone", "flashlight"},
            },
            {
                    {"shelves", "cannedfood", "notebook", "rock"},
                    {"backroom", "100dollarbill", "radio"},
            }
    };
    // Another 3d array, first index for node, 2nd index for arrays that store the item name / examine property.
    public static String[][][] examineList ={
            {
                    {"cabinet", "A sturdy metal cabinet."},
                    {"desk", "A desk."},
                    {"smartphone", "A Iphone."},
                    {"gun", "Get's you out of every situation."},
                    {"flashlight", "Powerful flashlight. Full batteries. Useful when dark."},
                    {"knife", "The classic survival weapon."},
            },
            {
                    {"backroom", "A creepy store backroom"},
                    {"gasstation", "A broken down gas station."},
                    {"shelves", "Study metal shelves"},
                    {"cannedfood", "Canned Peaches."},
                    {"notebook", "An empty notebook."},
                    {"100dollarbill", "Currency."},
                    {"radio", "Handheld radio, full batteries. Potentially useful in future situations."},
                    {"rock", "A rock, pretty useless"},

            }
    };
    //What the command agent looks for when user puts in input.
    public static String[] commandHeaders = {"examine", "open", "take", "scout"};
    public static String[][][] storyScript = { // The story script 3d array once again same concept.
            // Script Format: Script, (STRING) Speaker, (INT) Yield (Each character),(INT) Delay, (BOOLEAN) continue on same line or new line, (BOOLEAN) Event Before Line Print, (BOOLEAN)Has Event, (STRING) EventID
            {
                    {"You alright!?",    "???", "0.05", "2.5", "false", "None", "false", ""},
                    {"Yeah.",           "You", "0.05", "0.3", "false", "None", "false", ""},
                    {" What happened??", "You", "0.04", "0.8", "true", "None", "false", ""},
                    {" And who are you?!", "You", "0.04", ".7", "true", "None", "false", ""},
                    {"It's Alex!", "???", "0.06", ".4", "false", "None", "false", ""},
                    {" Your roommate.", "???", "0.05", ".2", "true", "None", "false", ""},
                    {" We need to get you help immediately!", "Alex", "0.05", "2.5", "true", "None", "false", ""},
                    {"Stay here.", "Alex", "0.06", "0.6", "false", "None", "false", ""},
                    {" I'm going to try to get help.", "Alex", "0.05", "3.5", "true", "false", "true", "PlaneTutorial"},
                    {"We have to leave now! ", "Alex", "0.05", "0.3", "false", "None", "true", ""},
                    {"The entire neighborhood!", "Alex", "0.06", "0.5", "true", "None", "false", ""},
                    {" It-", "Alex", "0.1", "0.1", "true", "None", "false", ""},
                    {"It's gone!", "Alex", "0.05", "0.8", "true", "None", "false", ""},
                    {"What do you mean it's gone?", "You", "0.07", "1", "false", "None", "false", ""},
                    {"Dude! ", "Alex", "0.07", "0.1", "false", "None", "false", ""},
                    {"Did you not see the meteor!", "Alex", "0.06", "0.3", "true", "None", "false", ""},
                    {" The neighborhood!", "Alex", "0.06", "0.4", "true", "None", "false", ""},
                    {" It looks like a", "Alex", "0.05", "0.3", "true", "None", "false", ""},
                    {"-a", "Alex", "0.3", "0.1", "true", "None", "false", ""},
                    {" giant blob of apple pudding!", "Alex", "0.05", ".8", "true", "None", "false", ""},
                    {"We dont' have enough time hurry up and pack your bag.", "Alex", "0.05", ".8", "false", "false", "true", "WeaponChoosing"},
                    {"You ready?", "Alex", "0.05", ".8", "false", "false", "false", ""},
                    {"Yea. I packed ", "You", "0.05", ".05", "false", "false", "true", "inventoryEvent"},
                    {"Ok, ", "Alex", "0.05", ".4", "false", "false", "false", ""},
                    {"where should we travel to? A. Gas Station, B. Airport", "Alex", "0.09", "1.4", "true", "false", "true", "choiceEvent"},
            },
            {
                    {"Ok, let's go to the gas station.", "Alex", "0.06", "2.5", "false", "None", "false", ""},
                    {"Hurry! Get into the car!", "Alex", "0.07", "1", "false", "false", "false", ""},
                    {"We have to get as many supplies as possible!", "Alex", "0.07", "1.5", "false", "false", "true", "gasStationSupplies"},
                    {"Okay, that's enough of supplies, time to find Alex.", "You", "0.04", "2.5", "false", "false", "true", "gunShotEvent"},
            },
            {
                    {"Ok, let's go to the Airport.",    "Alex", "0.05", "2.5", "false", "None", "false", ""},
                    {"I think I see an aircraft inside the hanger!", "You", "0.05", "2.5", "false", "None", "false", ""},
            },
            {
                    {"Hey!", "You", "0.04", "0.7", "false", "false", "false", ""},
                    {" Let him go! Now!", "You", "0.04", "0.7", "true", "false", "true", "doubleDeath"},
            },
            {
                    {"Alex has already lived a wonderful life. ", "You", "0.04", "0.5", "false", "false", "false", ""},
                    {" I wouldn't want him to suffer through the end of the world.", "You", "0.04", "1.5", "true", "false", "false", ""},
                    {"I have to get on that spacecraft to survive.", "You", "0.05", "1.5", "false", "false", "true", "radioEvent"},
                    {"Hey, is this the space craft launch site??!", "You", "0.05", "1.5", "false", "false", "false", ""},
                    {"Yes.", "Man In Suit", "0.05", "0.5", "false", "false", "false", ""},
                    {" What is your name sir?", "Man In Suit", "0.05", "1", "true", "false", "false", ""},
                    {"Uhhh. ", "You", "0.08", "0.5", "false", "false", "false", ""},
                    {" It is Barack Obama.", "You", "0.09", "1.5", "true", "false", "false", ""},
                    {"Wonderful,", "Man In Suit", "0.05", ".5", "false", "false", "false", ""},
                    {" Mr. Obama we will now be doing an IQ test.", "Man In Suit", "0.05", "1.5", "true", "false", "false", ""},
                    {"To Verify you are in fact Mr. Obama.", "Man In Suit", "0.08", "1.5", "false", "false", "true", "IQTest"},
                    {"You are not Barack Obama!", "Man In Suit", "0.05", "1", "false", "false", "false", ""},
                    {" Get out of here!", "Man In Suit", "0.05", "1", "true", "false", "false", ""},
            },
            {
                    {"Wonderful,", "Man In Suit", "0.05", "1", "false", "false", "false", ""},
                    {" welcome aboard to the new civilization.", "Man In Suit", "0.07", ".5", "true", "false", "false", ""},
            }

    };
    /**
     *  Plays the IQGame for players
     *  pre: gameType != null
     *  post: (BOOLEAN) true (if won) false (if lost)
     */
    public static boolean gameService(String gameType){
        Scanner inputScanner = new Scanner(System.in);
        if(gameType.equals("IQTest")){
            final int capIQ = 190, maxFails = 5; // declare constants that base the game.
            System.out.print("\n||IQ TEST||\n");
            System.out.printf("You have to pass this IQ test, you are only allowed to get %d questions incorrect\n", maxFails);
            System.out.print("GOOD LUCK!\n");
            wait(2000);
            Random rn = new Random(); // create new random
            int maxQuestions = questionList.length, startingIQ = 0, numberOfFails = 0;
            while(startingIQ <= capIQ){ // while not at IQ cap.
                try {
                    int randomNumber = rn.nextInt(maxQuestions);
                    System.out.printf("\n%s\n", questionList[randomNumber][0]);
                    wait(1000);
                    System.out.print(">>");
                    String userInput = inputScanner.nextLine();
                    if(userInput.trim().toUpperCase().equals(questionList[randomNumber][1].toUpperCase())){ // if it equals to answer
                        System.out.print("CORRECT!\n");
                        int iqGained = Integer.parseInt(questionList[randomNumber][2]); // parse into integer so I can get IQ points gained
                        startingIQ += iqGained;
                        System.out.printf("Current IQ %d (IQ NEEDED %d)", startingIQ, capIQ);
                    }else{
                        numberOfFails++;
                        if(numberOfFails == maxFails){ // If reached max fails, the player lost
                            return false;
                        }
                        System.out.print("INCORRECT!\n");
                        System.out.printf("Current IQ %d (IQ NEEDED %d)", startingIQ, capIQ);
                        System.out.printf("Number of fails %d out of %d", numberOfFails, maxFails);
                    }
                }catch(Exception e){
                    System.out.print("Invalid Input. Please Try Again.\n"); // Catch the invalid input.
                }
            }
            return true;

        }
        return false;
    }
    /**
     *  writes to file and displays to users the outcome of the game.
     *  pre:  result != null
     *  post: none (void)
     */
    public static void gameOver(String result) {
        visited[currentNode] = false; // set last one to false
        if(result == "win"){
            visited[currentNode] = true; // they won set it to true.
        }
        File file = new File("results.txt");
        if (!file.exists()) { // doesn't exist file
            try {
                file.createNewFile(); // create new file
            }catch(IOException ex){ // handle exception.
                System.out.println("Could not create file.");
            }
        }
        PrintWriter output = null; // Set it to null.
        try{
            output = new PrintWriter(file); //Try to create a new file
        }catch(FileNotFoundException ignored){

        }
        output.println("____START OF GAME____"); // The start of a game
        for (int i = 0; i < visited.length; i++) {
            if (visited[i]) { // Input all the game data
                switch (i) {
                    case 0:
                        output.println("BEGINNING [COMPLETED]\n");
                        break;
                    case 1:
                        output.println("GAS STATION [COMPLETED]\n");
                        break;
                    case 4:
                        output.println("FIND SPACESHIP [COMPLETED]\n");
                        break;
                    case 8:
                        output.println("BECOME BARACK OBAMA [COMPLETED]\n");
                        break;
                    case 2:
                        output.println("DISCOVER THE AIRCRAFT [COMPLETED]\n");
                        break;
                }
            } else { // to all the incompleted levels.
                switch (i) {
                    case 0:
                        output.println("BEGINNING [INCOMPLETED]\n");
                        break;
                    case 1:
                        output.println("GAS STATION [INCOMPLETED]\n");
                        break;
                    case 4:
                        output.println("FIND SPACESHIP [INCOMPLETED]\n");
                        break;
                    case 8:
                        output.println("BECOME BARACK OBAMA [INCOMPLETED]\n");
                        break;
                    case 2:
                        output.println("DISCOVER THE AIRCRAFT [INCOMPLETE]\n");
                        break;
                }
            }
        }
        output.println("____END OF GAME____"); // Set end of game bar
        output.close(); // close the stream.
        System.out.printf("\n FILE UPDATED \n"); // tell user that it has been saved;
        if(result.equals("win")){
            System.out.print("\n|||||||||||||||CONGRATULATIONS YOU SURVIVED THE END OF THE WORLD|||||||||||||||\n"); // output that the users won
        }else{
            System.out.print("\n|||||||||||||||GAME OVER|||||||||||||||\n"); // output that the users lost
        }
        System.out.print("\nIf you want to play again.\nPlease enter a node at where you want to jump back in at:\n");
        for(int i = 0; i < visited.length; i++){
            if(visited[i]){ // check if the current node is visited or not
                switch(i){ // output values to be entered by user.
                    case 0:
                        System.out.print("\nType \"0\" to jump back to the beginning\n");
                        break;
                    case 1:
                        System.out.print("Type \"1\" to jump back to the gas station\n");
                        break;
                    case 3:
                        System.out.print("Type \"3\" to jump back to the saving Alex at gas station\n");
                        break;
                    case 4:
                        System.out.print("Type \"4\" to jump back to the IQTest part\n");
                        break;
                    case 2:
                        System.out.print("Type \"2\" to jump back to the airport scene\n");
                        break;
                }
            }
        }
        while(true) {
            try {
                System.out.print("\n>>");
                Scanner inputScanner = new Scanner(System.in); // attempt to get input
                int userInput = inputScanner.nextInt();
                if(userInput < TOTAL_NODES && visited[userInput]){ // if the input is less than total nodes and is already visited.
                    currentNode = userInput; // set current node to the selected node.
                    traverseNode(userInput, 0); // jump back to the node.
                }
                break;
            } catch (Exception e) {
                System.out.print("Invalid Input. Please Try Again"); // catch the invalid input.
            }
        }
    }
    /**
     *  Outputs narration to the user
     *  pre: 0 < node < MAX_NODES, line != null, isBefore == true || isBefore == false.
     *  post: none (void)
     */
    public static void narratorService(int node, String line, boolean isBefore){
        if(!isBefore) { // if the narration should come before the dialogue is printed.
            switch (node) { // Cases printed out for each node / dialogue line
                case 0:
                    if (line.equals(" I'm going to go get help.")) {
                        System.out.print("\n---/\n");
                        System.out.print("Alex hastily runs out of the house, nearly trampling on the rubble.\nLeaving you alone on the floor.\n");
                        System.out.print("---/\n");
                        wait(4000);
                    }
                case 1:
                    switch (line) {
                        case "Hurry! Get into the car!":
                            System.out.print("\n---/\n");
                            wait(1000);
                            System.out.print("After 40 minutes of driving and dodging holes in the ground.\nYou have reached the gas station\n");
                            wait(1000);
                            System.out.print("---/\n");
                            wait(2000);
                            break;
                        case "Okay, that's enough of supplies, time to find Alex.":
                            System.out.print("\n---/\n");
                            System.out.print("While returning back to Alex.\n");
                            wait(1000);
                            System.out.print("You hear a gun shot and screaming.\n");
                            wait(2000);
                            System.out.print("You quickly duck behind a shelf and you see a man demanding Alex all his supplies with a gun.\n");
                            wait(2000);
                            System.out.print("---/\n");
                            wait(1000);
                            break;
                        case " Let him go! Now!":
                            System.out.print("\n---/\n");
                            System.out.print("You run up to the man threatening him to let go of Alex.\n");
                            System.out.print("---/\n");
                            wait(4000);
                            break;
                    }
            }
        }else{ // events that should be printed after the line is printed.
            switch (node){ // check the node.
                case 0:
                    if (line.equals("You alright!?")) {
                        System.out.print("---/\n");
                        System.out.print("You strain your eyes open. Smoke is everywhere.\nYour head hurts like crazy.\nSlowly a man walks towards you.\n");
                        System.out.print("---/\n");
                        wait(4000);
                    }else if(line.equals("We have to leave now! ")){
                        System.out.print("---/\n");
                        System.out.print("Alex returns back inside of the house.\n");
                        System.out.print("---/\n");
                        wait(5000);
                    }
                case 4:
                    switch (line) {
                        case "I have to get on that spacecraft to survive.":
                            if (!inventoryManager("hasItem", "radio")) { // check if player has radio.
                                System.out.print("\n---/\n");
                                System.out.print("You walked for miles and miles\n");
                                wait(2000);
                                System.out.print("No civilization is in site\n");
                                wait(3000);
                                System.out.print("It sure would have been a lot more easier if you had a radio.\n");
                                wait(2000);
                                System.out.print("---/\n");
                                gameOver("lost"); // the user lost the game.
                                return;
                            } else {
                                System.out.print("\n---/\n");
                                System.out.print("You listened on your radio for days\n");
                                wait(3000);
                                System.out.print("Luck strikes! There is a mention of a space craft ready to take off\n");
                                wait(3000);
                                System.out.print("It is located at a pair of coordinates\n");
                                wait(2000);
                                if (inventoryManager("hasItem", "smartphone")) { // check if user has this item.
                                    System.out.print("Good thing you have a smartphone!\n");
                                    wait(2000);
                                    System.out.print("You head towards the coordinates with help from your smartphone\n");
                                    wait(1000);
                                    System.out.print("-----/\n");
                                } else {
                                    System.out.print("Too bad you don't instructions on how to get there\n");
                                    wait(2000);
                                    System.out.print("You spend your last days aimlessly wondering around, being optimistic that you somehow walk yourself to safety randomly\n");
                                    wait(3000);
                                    System.out.print("----/\n");
                                    gameOver("lost"); // the user lost the game
                                    return;
                                }
                            }
                            break;
                        case "Hey, is this the space craft launch site??!":
                            System.out.print("\n---/\n");
                            System.out.print("After walking for what it felt like forever.\n");
                            wait(2000);
                            System.out.print("You have finally reached the launch site.\n");
                            wait(2000);
                            System.out.print("You see a man in a suit standing near one of the entrances.\n");
                            wait(3000);
                            System.out.print("It seems like you have to have world importance to be accepted in.\n");
                            System.out.print("---/\n");
                            wait(5000);
                            break;
                        case " Get out of here!":
                            System.out.print("\n---/\n");
                            System.out.print("You watch as the space craft takes off.\n");
                            wait(2000);
                            System.out.print("You were so close of making it in.\n");
                            wait(2000);
                            System.out.print("Better luck next time.\n");
                            wait(3000);
                            System.out.print("---/\n");
                            wait(5000);
                            gameOver("lost"); // the user has lost the game.
                            return;
                    }
                case 2:
                    switch(line){
                        case "Ok, let's go to the Airport.":
                            System.out.print("\n---/\n");
                            System.out.print("You and Alex reached the airport.\n");
                            wait(2000);
                            System.out.print("You and Alex find a hanger.\n");
                            System.out.print("---/\n");
                        case "I think I see an aircraft inside the hanger!":
                            if(inventoryManager("hasItem", "flashlight")){
                                System.out.print("\n---/\n");
                                System.out.print("Good thing you brought a flashlight\n");
                                wait(2000);
                                System.out.print("With the flashlight you managed to find the keys of the aircraft\n");
                                wait(2000);
                                System.out.print("You and Alex started up the aircraft and flew it across the world.\n");
                                wait(2000);
                                System.out.print("Although you both knew that you only had a couple of hours of fuel left to live\n");
                                wait(2000);
                                System.out.print("The views made it worth it.");
                                wait(2000);
                                System.out.print("\n---/\n");
                                gameOver("win");
                                return;
                            }else{
                                System.out.print("\n---/\n");
                                System.out.print("It was too dark to find and startup the aircraft.\n");
                                wait(2000);
                                System.out.print("You and Alex spent hours feeling stuff in the dark trying everything to get the aircraft air-ready.\n");
                                wait(2000);
                                System.out.print("Slowly both of you got eaten up by the Earth");
                                wait(2000);
                                System.out.print("\n---/\n");
                                gameOver("lost");
                                return;
                            }
                    }
                case 5:
                    if (" welcome aboard to the new civilization.".equals(line)) {
                        System.out.print("\n---/\n");
                        System.out.print("You enter the space craft\n");
                        wait(2000);
                        System.out.print("You are guided into your master suite.\n");
                        wait(2000);
                        System.out.print("Slowly the space craft blasts off the fireball you once called Earth");
                        wait(3000);
                        gameOver("win"); // the user has won the game!
                        return;
                    }
            }
        }
    }
    /**
     *  Prints out the description for requested item
     *  pre: object, typ != null
     *  post: none (void)
     */
    public static void descriptionService(String object, String typ){
        String currentPlayerLocation = locationList[LOCATION_INTEGER][0];
        if(typ.equals("location")) { // if the request is location.
            switch (object) { // check cases one by one.
                case "area":
                    // check current location
                    if ("house".equals(locationList[LOCATION_INTEGER][0])) {
                        System.out.print("\n-----\n");
                        System.out.print("The entire house is filled with debris.\nLight comes through a hole in the ceiling.\n");
                        System.out.print("-----\n");
                        wait(5000);
                    }
            }
        }
    }
    /**
     *  prints out examined item
     *  pre: secondaryString != null
     *  post: none (void)
     */
    public static void examineManager(String secondaryString){
        secondaryString = secondaryString.trim().toLowerCase(); // make it entirely lowercase and trim whitespace.
        switch(secondaryString){ // check case by case
            case "area":
                descriptionService("area", "location");
        }
        for(int i = 0; i < examineList[currentNode].length; i++){ // go through the examine list
            if(examineList[currentNode][i][0].equals(secondaryString)){ // if it equals to the requested string.
                System.out.printf("\n%s\n", examineList[currentNode][i][1]); // print out examination description.
            }
        }
    }
    /**
     *  Prints out objects that are on top of requested Item.
     *  pre: requestItem != null
     *  post: none (void)
     */
    public static void viewManager(String requestedItem){
        for(int i = 0; i < itemList[currentNode].length; ++i){
            if(itemList[currentNode][i][0].equals(requestedItem)){
                System.out.printf("\nYou inspect the %s and you see these items: \n", requestedItem);
                for(int j = 1; j < itemList[currentNode][i].length; ++j){
                    System.out.printf("| %s ", itemList[currentNode][i][j]);
                }
                System.out.print("|");
                System.out.println();
            }
        }
    }
    /**
     *  Follows requests based on item input.
     *  pre: request != null, item != null
     *  post: boolean (true if item is found) (false if item is not found)
     */
    public static boolean inventoryManager(String request, String item){
        item = item.trim().toLowerCase();
        if(request.equals("view")){
            if(Arrays.stream(playerInventory[currentNode]).allMatch(Objects::isNull)){ // if entire node array is null then it means its empty.
                System.out.printf("\n%s\n", "Your inventory is empty!"); // Tell user inventory is empty.
            }else{
                System.out.printf("\n%s", "Your Inventory Contains: ");
                for(int i = 0; i < playerInventory[currentNode].length; i++){
                    if(playerInventory[currentNode][i] != null) {
                        System.out.printf("| %s %s ", playerInventory[currentNode][i].matches("[aeiou]") ? "an" : "a", playerInventory[currentNode][i]);
                    }
                }
                System.out.println();
            }
        }else if(request.equals("grab")){
            if(Arrays.stream(playerInventory[currentNode]).noneMatch(Objects::isNull)){ // if inventory is has no nulls it means that it is full.
                System.out.printf("\n%s\n", "Your inventory is full, try to remove some items.");
            }else if(Arrays.asList(playerInventory[currentNode]).contains(item)){ // check if item exists or not already.
                System.out.printf("\n%s already exists in your inventory\n", item);
            }
            else{
                for(int i = 0; i < playerInventory[currentNode].length; i++){ // go through entire inventory
                    if(playerInventory[currentNode][i] == null){ // if there is an empty space (null).
                        for(int j = 0; j < examineList[currentNode].length; ++j){ // go through the examine list.
                            if(examineList[currentNode][j][0].equals(item)){ // if the examine list item name equals to the item it means that the item exists.
                                playerInventory[currentNode][i] = item; // store that item inside inventory
                                System.out.printf("\n%s %s\n", "You have grabbed the", item); // tell the player that the item is in their inventory,
                                return true; // return true (the item was put into inventory)
                            }
                        }
                    }
                }
                System.out.printf("\nYou weren't able to grab the %s.\n", item); // Tell user that they weren't able to grab item.
            }
        }else if(request.equals("remove")){ // if the request is remove
            for(int i = 0; i < playerInventory[currentNode].length; ++i){
                if(playerInventory[currentNode][i] != null) { // if the current spot is not empty
                    if (playerInventory[currentNode][i].equals(item)) { // if it equals to the item
                        System.out.printf("\nSuccessfully removed removed %s\n", item); // tell user that it was deleted.
                        playerInventory[currentNode][i] = null; // set the spot as empty
                        return true; // return true (the item was removed).
                    }
                }
            }
            System.out.printf("\n%s does not exist in your inventory\n", item); // tell the user that it does not exist
        }else if(request.equals("silentView")){ // The current request is to return items without prompt.
            if(Arrays.stream(playerInventory[currentNode]).allMatch(Objects::isNull)){ // if all items are null it means empty.
                System.out.printf("%s", "nothing!"); // no objects inside inventory.
            }else{
                for(int i = 0; i < playerInventory[currentNode].length; i++){ // go through inventory of current node
                    if(playerInventory[currentNode][i] != null) { // if the inventory space isn't empty
                        System.out.printf("| %s ", playerInventory[currentNode][i]); // print out the item
                    }
                }
                System.out.print("|"); // print the last bar
            }
        }else if(request.equals("hasItem")){ // check if player has an item
            if(Arrays.stream(playerInventory[currentNode]).allMatch(Objects::isNull)){ // if the inventory is all nulls (empty) return false
                return false;
            }else{
                for(int i = 0; i < playerInventory[currentNode].length; i++){ // go through inventory of current node.
                    if(playerInventory[currentNode][i] != null){ // if the current inventory node isn't empty.
                        if(playerInventory[currentNode][i].trim().toLowerCase().equals(item.trim().toLowerCase())){ // if it equals to the item.
                            return true; // we found the item.
                        }
                    }
                }
            }
        }
        return false; // default is to return false if none is true.
    }
    /**
     *  checks if current input is a command or not
     *  pre: commandLine != null, expectedInput.Length > 0, dataType != null;
     *  post: none (void)
     */
    public static void commandManager(String commandLine, String[] expectedInput, String dataType){
        String firstWord = "", secondWord = null; // start off with declaring first word and second.
        try { // try to split the word into two words
            String inputSplit[] = commandLine.trim().split(" ", 2); // regex space inbetween
            firstWord = inputSplit[0];
            secondWord = inputSplit[1];
            switch(firstWord){
                case "examine": // if examine
                    examineManager(secondWord); // run examine method with passed through second word
                    break;
                case "grab": // if player wants to grab
                    inventoryManager("grab", secondWord); // grab the second word
                    wait(1000); // wait 1 second
                    break;
                case "remove":// if the player wants to remove
                    inventoryManager("remove", secondWord); // run the method to remove.
                    wait(1000); // wait 1 second.
                    break;
                case "view": // if the palyer wants to view an item
                    viewManager(secondWord); // run the method to view item.
                    wait(1000); // wait 1 second.
                    break;
            }
        }
        catch(Exception e){ //couldn't split into two must mean input has more than 2 words or less than 2.
            switch(firstWord){  // first word commands.
                case "help": // output help commands.
                    System.out.print("\ntype \"scout\" to look around yourself for objects.\n");
                    System.out.print("type \"view\" followed by an object to view what's on the object{EG: view table}\n");
                    System.out.print("type \"grab\" followed by the object to put it in your inventory {EG: grab bread}\n");
                    System.out.print("type \"examine\" followed by the object to examine it.\n");
                    System.out.print("type \"inventory\" to view your inventory.\n");
                    System.out.print("type \"remove\" followed by the object to remove it from your inventory. {EG: remove gun}\n");
                    System.out.print("type \"exit\" the current layer of the command line.\n");
                    break;
                case "view": // the player incorrectly put view without a second letter.
                    System.out.print("\nPlease type an object after \"view\" {EG: view car}\n");
                    break;
                case "scout": // the player want's to scout the current area.
                    System.out.print("\nYou you look around you and you see");
                    for(int i = 1; i < locationList[currentNode].length-1; i++){ // go through all items
                        //NOTE: I know this is incorrect, you base "an" and "a" based on sound, but this is good/close enough!
                        System.out.printf(" %s %s,", locationList[currentNode][i].matches("[aeiou]") ? "an" : "a" ,locationList[currentNode][i]);
                    }
                    System.out.printf(" and %s %s\n", locationList[currentNode][locationList[currentNode].length-1].matches("[aeiou]") ? "an" : "a", locationList[currentNode][locationList[currentNode].length-1]);
                    wait(2000); // wait 2 seconds
                    break;
                case "grab": // incorrect usage of grab
                    System.out.print("\nPlease specify an object after \"grab\" {EG: grab knife}\n");
                    break;
                case "examine": // incorrect usage of examine
                    System.out.print("\nPlease specify an object after \"examine\" {EG:examine brick}\n");
                    break;
                case "inventory": // incorrect usage of inventory.
                    inventoryManager("view", "None");
                    wait(1000);
                    break;
                case "remove": // incorrect usage of remove.
                    System.out.print("\nPlease specify an object after \"remove\" {EG:remove knife}\n");
                    break;
            }
        }
    }
    /**
     *  Pauses the thread for x milliseconds
     *  pre: 0 < ms < INT_MAX;
     *  post: none (void)
     */
    public static void wait(int ms){ // sleep the current thread for x milliseconds.
        try{
            Thread.sleep(ms); // Yield the thread for x milliseconds.
        }
        catch(InterruptedException ex){ // catch exception
            Thread.currentThread().interrupt();
        }
    }
    /**
     *  outputs the dialogue in typewriter effect.
     *  pre: speaker != null, storyline != null, 0 < textDelay < INT_MAX, sameLine == true || sameLine == false
     *  post: none (void)
     */
    public static void outputLine(String speaker, String storyLine, int textDelay, boolean sameLine){
        if (!sameLine){ // if don't print on same line
            System.out.printf("\n(%s):\n", speaker); // print out new speaker header
        }
        for(int i = 0; i < storyLine.length(); i++){ // now print out the dialogue
            System.out.printf("%c", storyLine.charAt(i)); // print character by character.
            wait(textDelay); // yield for textDelay milliseconds.
        }
    }
    /**
     *  checks if Input is valid or not
     *  pre: 0 < expectedInput.length, dataType != null
     *  post: false (for invalid input), true (for valid input)
     */
    public static boolean inputManager(String[] expectedInput, String dataType){
        System.out.printf("%s ", ">>"); // output Commandline symbol.
        Scanner inputScanner = new Scanner(System.in);
        String userInput = inputScanner.nextLine();
        if(!dataType.equals("choiceSelection")) { // if the requested datatype is for a choice selection.
            commandManager(userInput, expectedInput, dataType); // run command for choice selection
        }
        for (String s : expectedInput) { // go through expected Inputs
            if (userInput.toUpperCase().trim().equals(s.toUpperCase())) { // trim and make it all equal case, if they are the same then we have a match
                if (dataType.equals("choiceSelection")) { // if the datatype is a choice selection then get the choice and store it into the global node choice variable.
                    nodeChoice = s.toUpperCase();
                    return true; // we found a match in input
                }
                return true; // its true
            }
        }
        return false; // we didn't find a match
    }
    /**
     *  outPuts the goal until the user completes the goal
     *  pre: userQuery != null, expectedOutPut.length > 0, dataType != null
     *  post: (none) void
     */
    public static void outputManager(String userQuery, String[] expectedOutPut, String dataType){
        System.out.println(); // print a new line
        System.out.printf("%s\n", userQuery); // print out the query
        while(!inputManager(expectedOutPut, dataType)){ // while the input doesn't equal to query keep on asking.
            System.out.printf("%s\n", userQuery); // tell user to get this goal.
        }
    }
    /**
     *  checks if event is valid, and plays the event if it is.
     *  pre: eventID != null.
     *  post: boolean (true if event was played, false if not)
     */
    public static boolean eventManager(String eventID){
        switch(eventID){ // use switch statement for events
            case "PlaneTutorial": // the starting tutorial
                String[] acceptedOutputs = new String[]{"examine area"}; // create array with acceptable outputs.
                outputManager("Type \"examine area\" to check out the current area", acceptedOutputs, "string" ); // run the method for the tutorial.
                return true; // return true if the event went good
            case "WeaponChoosing": // the weapon choosing tutorial
                acceptedOutputs = new String[]{"exit"}; // acceptable outputs is "exit".
                outputManager("Look around the room for weapons [type \"help\" for commands].", acceptedOutputs, "string"); // run the method for the current event
                return true; // the event went well
            case "inventoryEvent": // inventory event
                inventoryManager("silentView", "None"); // print out the inventory silently.
                wait(1000); // wait 1 second
                return true; // event went well
            case "choiceEvent": // if the event is choice Event
                acceptedOutputs = new String[]{"A", "B"}; // accepted input is either first option or second (A, B)
                outputManager("Choose a location to go to type \"A\" to go to the first location and \"B\" for the second location.", acceptedOutputs, "choiceSelection"); // run the method for current event.
                return true; // event went well.
            case "gasStationSupplies": // gas station event
                acceptedOutputs = new String[]{"exit"}; // same command line classic acceptedOutput
                outputManager("Look around the convenience store for supplies [type \"help\" for commands].", acceptedOutputs, "string"); // run the method for the current event.
                return true;
            case "gunShotEvent": // Gunshot event (choose your own path)
                acceptedOutputs = new String[]{"A", "B"};
                outputManager("Type \"A\" to attempt to save Alex or \"B\" to ditch him and escape.", acceptedOutputs, "choiceSelection");
                return true;
            case "doubleDeath": // gas Station death event
                if(inventoryManager("hasItem", "gun")){ // if the user has a gun
                    System.out.print("\n-----/\n");
                    System.out.print("Good thing you brought a gun.\n");
                    wait(3000);
                    System.out.print("You attempt to shoot the man\n");
                    wait(3000);
                    System.out.print("But sadly the gun had no bullets.\n");
                    wait(3000);
                    if(inventoryManager("hasItem", "knife")){ // if the user has a knife
                        System.out.print("All luck isn't lost though!\n");
                        wait(3000);
                        System.out.print("Good thing you brought a knife!\n");
                        wait(3000);
                        System.out.print("You lunge at the man, but Alex gets shot just as you get to the man.\n");
                        wait(3000);
                        System.out.print("You get shot too in the process.\n");
                        if(inventoryManager("hasItem", "radio")){ // if the user has a radio
                            int lastNode = currentNode;
                            currentNode = 4; // merge and allow the user to go back into the 4th node
                            playerInventory[currentNode] = playerInventory[lastNode].clone();
                            traverseNode(currentNode, 2);
                        }else{
                            System.out.print("You spend your last miserable days in the gas station, waiting for help which would never come.\n");
                            wait(3000);
                            System.out.print("Perhaps a radio would have worked things out.\n");
                            System.out.print("-----\n");
                            gameOver("lost");
                        }
                    }
                }
                if(inventoryManager("hasItem", "knife")){ // check if user has a knife.
                    System.out.print("\n-----\n");
                    System.out.print("Good thing you a knife!\nYou attempt to stab the man, but sadly he shoots Alex before you can get to him.\nYou get shot in the process.\n");
                    wait(2000);
                    if(inventoryManager("hasItem", "radio")){ // if the user has a radio
                        int lastNode = currentNode;
                        currentNode = 4; // merge and allow the user to go back into the 4th node
                        playerInventory[currentNode] = playerInventory[lastNode].clone();
                        traverseNode(currentNode, 2);
                        return true;
                    }else{
                        System.out.print("You spend your last miserable days in the gas station, waiting for help which would never come.\n");
                        wait(3000);
                        System.out.print("Perhaps a radio would have worked things out.\n");
                        System.out.print("-----\n");
                        gameOver("lost");
                    }
                    return true;
                }else{
                    System.out.print("\n-----\n");
                    System.out.print("You run up to the man and attempt to punch him.\n");
                    wait(3000);
                    System.out.print("How foolish.\n");
                    wait(3000);
                    System.out.print("Never bring a fist to a gun fight.\n");
                    wait(3000);
                    System.out.print("Next time maybe bring something?\n");
                    wait(3000);
                    System.out.print("At least you died a hero.\n");
                    System.out.print("-----\n");
                    gameOver("lost");
                    return true;
                }
            case "IQTest": // iq test game
                if(gameService("IQTest")){
                    currentNode = 5; // set new node.
                    return true;
                }else{
                    return false;
                }


        }
        return false; // event went bad
    }
    /**
     *  plays out events based on node
     *  pre: 0 < node, currentLine < INT_MAX;
     *  post: none (void)
     */
    public static void nodeEvents(int node, int currentLine){
        for(; currentLine < storyScript[node].length; ++currentLine){ // start from requested line and play up to the max
            String scriptLine = storyScript[node][currentLine][0]; // the script string itself.
            narratorService(node, scriptLine, true); // check if narration is needed for this.
            String scriptNarrator = storyScript[node][currentLine][1]; // get the narrator
            String currentEventID = storyScript[node][currentLine][7]; // get the event ID
            int textDelay = (int)(Double.parseDouble(storyScript[node][currentLine][2]) * 1000); // convert to milliseconds.
            int Delay = (int)(Double.parseDouble(storyScript[node][currentLine][3]) * 1000); // convert to milliseconds.
            boolean sameLine = Boolean.parseBoolean(storyScript[node][currentLine][4].toLowerCase()); // parse into boolean (check if string should be printed on same line).
            boolean beforeOutput = Boolean.parseBoolean(storyScript[node][currentLine][5].toLowerCase()); // check if event should come out before line print. parse string into boolean
            boolean hasEvent = Boolean.parseBoolean(storyScript[node][currentLine][6].toLowerCase()); // check if the current line even has an event. parse string into boolean.
            if(beforeOutput && hasEvent){ // if there is an event and it should be ran before line print.
                eventManager(currentEventID); // run the event
            }
            outputLine(scriptNarrator, scriptLine, textDelay, sameLine); // typewriter effect the line
            wait(Delay); // wait for the delay
            narratorService(node, scriptLine, false); // check if the line needs narration
            if(!beforeOutput && hasEvent){ // check if there is an event and if it should be ran after line print
                if(currentEventID.equals("IQTest")){ // cases for end early events.
                    if(eventManager(currentEventID)){ // if its sucessful end early.
                        return;
                    }
                }else{
                    eventManager(currentEventID);
                }
            }
        }
    }
    /**
     *  Traverses the node and line.
     *  pre: 0 < node < TOTAL_NODES; 0 < line < storyScript[node][line].length
     *  post: none (void)
     */
    public static void traverseNode(int node, int line){
        if(storyGraph[node].size() != 0){ // if it has children
            visited[node] = true; // mark this as visited
            nodeEvents(node, line);// run the event
            if(nodeChoice != null){ // check if a choice is made
                if(nodeChoice.equals("A")){ // if the choice is first option
                    nodeChoice = null; // update node
                    int oldNode = currentNode;
                    currentNode = storyGraph[node].get(0); // get left child from adjacency list
                    playerInventory[currentNode] = playerInventory[oldNode].clone();
                    traverseNode(currentNode, 0); // now run the new node.
                }else{ // second choice is made
                    nodeChoice = null; // update node
                    int oldNode = currentNode;
                    currentNode = storyGraph[node].get(1); // get right child from adjacency list
                    playerInventory[currentNode] = playerInventory[oldNode].clone();
                    traverseNode(currentNode, 0); // now run the new node.
                }
            }else{ // no choice was made so go to the next child
                currentNode = storyGraph[node].get(0); // get next child from adjacency list.
                traverseNode(currentNode, 0);
            }
        }else{
            nodeEvents(node, line); // last child.
        }
    }
    /**
     *  sets up the game and starts up the graph / required variables.
     *  pre: none
     *  post: void (none)
     */
    public static void gameSetUp(){
        storyGraph = new LinkedList[TOTAL_NODES]; // 6 Nodes
        for (int i = 0; i < TOTAL_NODES; i++){ // create adjacency list.
            storyGraph[i] = new LinkedList();
        }
        storyGraph[0].add(1); // 0 -> 1
        storyGraph[0].add(2); // 0 -> 2
        storyGraph[1].add(3); // 1 -> 3
        storyGraph[1].add(4); // 1 -> 4
        storyGraph[4].add(5); // 4 -> 8
        // STORY GRAPH STRUCTURE: VISUALIZE
        /*
             (START_NODE [0])
                /        \
              (1)        (2)
             /  \
           (3)  (4)
                 \
                 (5)
         */
    }
    public static void main(String[] args) { // main method
        gameSetUp(); // setup the game
        outputManager("Type \"start\" To Begin.", new String[]{"start"} , "string" ); // start the game
        traverseNode(currentNode, 0); // traverse the first node
    }
}

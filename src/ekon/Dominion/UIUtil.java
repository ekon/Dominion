package ekon.dominion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import ekon.dominion.Card.CardType;

public class UIUtil {
  
  private static UIUtil uiUtil;
  private static BufferedReader br;
  
  public static UIUtil getUIUtil() {
	if (uiUtil == null) {
	  uiUtil = new UIUtil(new InputStreamReader(System.in));
	}
	return uiUtil;
  }
  
  public static void setUpForTest(Reader inputStream) {
	SecurityUtil securityUtil = new SecurityUtil();
	securityUtil.verifyCallingClassIsTest();
	
	uiUtil = new UIUtil(inputStream);
  }
  
  private UIUtil(Reader inputStream) {
	UIUtil.br = new BufferedReader(inputStream);
  }
  
  public void tellUser(String message) {
	System.out.println(message);
  }
  
  public boolean revealReaction(Player opponent) {
	Cards reactionCards = opponent.hand().getCards(CardType.REACTION);
	boolean playReaction = false;
	if (reactionCards.size() > 0) {
	  playReaction = getBooleanFromUser("Reveal Reaction? YES/NO. Reaction cards to choose from: " + reactionCards.toString());
	  if (playReaction) {
		// TODO(ekon): Play reaction card, as a reaction!
		// reactionCard.playReaction();
		// if playedReaction card, set playedReaction = true;
	  }
	}
	return playReaction;
  }
  
  public boolean getBooleanFromUser(String message, String posOption, String negOption) {
	while (true) {
	  String availableOptions = "Available options are: " + posOption + "/" + negOption;
	  String userInput = getUserInput(message, availableOptions);
	  try {
		if (userInput.equals(posOption)) {
		  return true;
		} else if (userInput.equals(negOption)) { return false; }
	  } catch (IllegalArgumentException e) {
		userInput = getUserInput(userInput + " is not an available option.", availableOptions);
	  }
	}
  }
  
  // True=YES, False=NO.
  public boolean getBooleanFromUser(String message) {
	return getBooleanFromUser(message, "YES", "NO");
  }
  
  public Card getCardFromUser(String input, Cards availableCards) {
	return getCardFromUser(input, availableCards, false);
  }
  
  public Card getCardFromUser(String message, Cards availableCards, boolean isNoneAnOption) {
	String userInput = getUserInput(message, availableCards);
	while (true) {
	  try {
		if (isNoneAnOption && isUserDone(userInput)) { return null; }
		Card chosenCard = Card.valueOf(userInput.replace(" ", "_").trim());
		if (availableCards.contains(chosenCard)) { return chosenCard; }
		userInput = getUserInput(userInput + " is not one of the available cards. AvailableCards are: ", availableCards);
	  } catch (IllegalArgumentException e) {
		userInput = getUserInput(userInput + " is an invalid selection. AvailableCards are: ", availableCards);
	  }
	}
  }
  
  public Card getCardCostingUpTo(int maxCost, Cards availableCards) {
	return getCardFromUser("Get a card costing up to " + maxCost + ".", availableCards);
  }
  
  public Cards getCardsFromUser(String message, Cards availableCards, boolean isNoneAnOption) {
	return getCardsFromUser(getUserInput(message, availableCards), availableCards, -1, isNoneAnOption);
  }
  
  /** @param requiredNumCards -1 if no amount is required */
  public Cards getCardsFromUser(String userInput, Cards availableCards, int requiredNumCards, boolean isNoneAnOption) {
	Cards cardsToReturn = null;
	while (cardsToReturn == null) {
	  try {
		// Create a copy of available cards to update when cards become no longer available.
		Cards newAvailableCards = new Cards(availableCards);
		cardsToReturn = new Cards();
		if (isNoneAnOption && isUserDone(userInput)) { return null; }
		String[] splitInput = userInput.split(",");
		if ((requiredNumCards != -1) && (splitInput.length != requiredNumCards)) {
		  userInput = getUserInput("You need to choose exactly " + requiredNumCards + " cards.", availableCards);
		  cardsToReturn = null;
		} else {
		  for (String input : splitInput) {
			Card chosenCard = Card.valueOf(input.replace(" ", "_").trim());
			cardsToReturn.add(chosenCard);
			if (!newAvailableCards.remove(chosenCard)) {
			  userInput = getUserInput("Card " + chosenCard + " is no longer available.", newAvailableCards);
			  cardsToReturn = null;
			}
		  }
		}
	  } catch (IllegalArgumentException e) {
		userInput = getUserInput(userInput + " is an invalid selection.", availableCards.toString());
	  }
	}
	return cardsToReturn;
  }
  
  private boolean isUserDone(String userInput) {
	return (userInput.equals("")) || (userInput.equals("DONE")) || (userInput.equals("NONE"));
  }
  
  private String getUserInput(String message, Cards availableCards) {
	return getUserInput(message, availableCards.toString());
  }
  
  private String getUserInput(String message, String availableOptions) {
	System.out.println(message + " Available options are: " + availableOptions);
	try {
	  String userInput = br.readLine().toUpperCase().trim();
	  tellUser("		You Entered: " + userInput);
	  return userInput;
	} catch (IOException ioe) {
	  System.out.println("IO error trying to read user input.");
	  System.exit(1);
	}
	return null;
  }
  
  public void writeCard(Card card) {
	tellUser("Playing " + card.name() + ": " + card.description());
  }
}

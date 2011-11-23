package ekon.dominion;

// @formatter:off
import static ekon.dominion.Card.CardType.*;
import static ekon.dominion.Card.SetType.*;

// @formatter:on
import java.util.Arrays;
import java.util.List;

// @formatter:off
public enum Card {
	// Treasure
	COPPER	 (0, 		BASE, 0, new CardType[] { TREASURE }, new TreasureActions(1)),
	SILVER 	 (3, 		BASE, 0, new CardType[] { TREASURE }, new TreasureActions(2)),
	POTION	 (4, true, 	ALCHEMY, 0, new CardType[] { TREASURE }, new TreasureActions(0)),
	GOLD	 (6, 		BASE, 0, new CardType[] { TREASURE }, new TreasureActions(3)),
	PLATINUM (9, 		PROSPERITY, 0, new CardType[] { TREASURE }, new TreasureActions(6)),
	
	// Victory
	ESTATE	( 2, BASE,  	  1, new CardType[] { VICTORY }, new Actions()),
	DUCHY	( 5, BASE,  	  3, new CardType[] { VICTORY }, new Actions()),
	PROVINCE( 8, BASE,  	  6, new CardType[] { VICTORY }, new Actions()),
	COLONY	(11, PROSPERITY, 10, new CardType[] { VICTORY }, new Actions()),	
	CURSE	( 0, BASE, 		 -1, new CardType[] { VICTORY }, new Actions()),
	
	// Actions
	TRANSMUTE	(0, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions("Trash a card from your hand. If it is an...\nAction card, gain a Duchy\nTreasure card, gain a Transmute\nVictory card, gain a Gold")),
	
	// TODO(ekon): Add this to the end-game logic.
	VINEYARD	(0, true, ALCHEMY, 0, new CardType[] { ACTION, VICTORY }, new Actions("Worth 1 Victory for every 3 Action cards in your deck (rounded down).")),
	
	CELLAR		(2, BASE, 0, new CardType[] { ACTION }, new Actions(0, 1, 0, 0, "Discard any number of cards. +1 card per card discarded.")),
	CHAPEL		(2, BASE, 0, new CardType[] { ACTION }, new Actions("Discard up to 4 cards from your hand.")),
	MOAT		(2, BASE, 0, new CardType[] { ACTION, REACTION }, new Actions(2, 0, 0, 0)),	
	
	COURTYARD		(2, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(3, 0, 0, 0, "Put a card from your hand on top of your deck.")),
	PAWN			(2, INTRIGUE, 0, new CardType[] { ACTION }, new Actions("Choose two: +1 Card; +1 Action; +1 Buy; +1 Coin. (The choices must be different.)")),
	SECRET_CHAMBER	(2, INTRIGUE, 0, new CardType[] { ACTION, REACTION }, new Actions("Discard any number of cards. +1 Coin per card discarded. When another player plays an attack card, you may reveal this from your hand. If you do, +2 Cards, then put 2 cards from your hand on top of your deck.")),
	
	EMBARGO			(2, SEASIDE, 0, new CardType[] { ACTION }, new Actions(0, 0, 2, 0, "Trash this card. Put an Embargo token on top of a Supply pile.\nWhen a player buys a card, he gains a Curse card per Embargo token on that pile.")),
	HAVEN			(2, SEASIDE, 0, new CardType[] { ACTION, DURATION }, new Actions(1, 1, 0, 0, "Set aside a card from your hand face down. At the start of your next turn, put it into your hand.")),
	LIGHTHOUSE		(2, SEASIDE, 0, new CardType[] { ACTION, DURATION, REACTION }, new Actions(0, 1, 1, 0, "Now and at the start of your next turn: +1 Coin.\nWhile this is in play, when another player plays an Attack card, it doesn't affect you.")),
	NATIVE_VILLAGE	(2, SEASIDE, 0, new CardType[] { ACTION }, new Actions(0, 2, 0, 0, "Choose one: Set aside the top card of your deck face down on your Native Village mat; or put all the cards from your mat into your hand. You may look at the cards on your mat at any time; return them to your deck at the end of the game.")),
	PEARL_DRIVER	(2, SEASIDE, 0, new CardType[] { ACTION }, new Actions(1, 1, 0, 0, "Look at the bottom card of your deck. You may put it on top.")),
	
	APOTHECARY		(2, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions(1, 1, 0, 0, "Reveal the top 4 cards of your deck. Put the revealed Coppers and Potions into your hand. Put the other cards back on top in any order.")),
	HERBALIST		(2, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions(0, 0, 1, 1, "When you discard this from play, you may put one of your Treasures from play on top of your deck.")),
	SCRYING_POOL	(2, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions(0, 1, 0, 0, "Each player (including you) reveals the top card of his deck and either discards it or puts it back, your choice. Then reveal cards from the top of your deck until revealing one that isn't an Action. Put all of your revealed cards into your hand.")),
	UNIVERSITY		(2, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions(0, 2, 0, 0, "You may gain an Action card costing up to 5 Coins.")),
	
	CHANCELLOR	(3, BASE, 0, new CardType[] { ACTION }, new Actions(0, 0, 2, 0, "You may immediately put your deck into the discard pile.")),
	VILLAGE		(3, BASE, 0, new CardType[] { ACTION }, new Actions(1, 2, 0, 0)),
	WOODCUTTER	(3, BASE, 0, new CardType[] { ACTION }, new Actions(0, 0, 2, 1)),
	WORKSHOP	(3, BASE, 0, new CardType[] { ACTION }, new Actions("Gain a card costing up to 4 coins.")),
	
	GREAT_HALL	(3, INTRIGUE, 1, new CardType[] { ACTION, VICTORY }, new Actions(1, 1, 0, 0)),
	MASQUERADE	(3, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(2, 0, 0, 0, "Each player passes a card from his hand to the left at once. Then you may trash a card from your hand.")),
	SHANTY_TOWN	(3, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(0, 2, 0, 0, "Reveal your hand. If you have no action cards in hand, +2 Cards.")),
	STEWARD		(3, INTRIGUE, 0, new CardType[] { ACTION }, new Actions("Choose one: +2 Cards; or +2 Coins; or trash 2 cards from your hand.")),
	SWINDLER	(3, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(0, 0, 2, 0, "Each other player trashes the top card of his deck and gains a card with the same cost that you choose.")),
	WISHING_WELL(3, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(1, 1, 0, 0, "Name a card. Reveal the top card of your deck. If it's the named card, put it into your hand.")),

	AMBASSADOR		(3, SEASIDE, 0, new CardType[] { ACTION, ATTACK }, new Actions("Reveal a card from your hand. Return up to 2 copies of it from your hand to the supply. Then each other play gains a copy of it.")),
	FISHING_VILLAGE	(3, SEASIDE, 0, new CardType[] { ACTION, DURATION }, new Actions(2, 0, 1, 0, "At the start of your next turn: +1 Action, +1 Coin.")),
	LOOKOUT			(3, SEASIDE, 0, new CardType[] { ACTION }, new Actions(0, 1, 0, 0, "Look at the top 3 cards of your deck. Trash one of them. Put one on top of your deck.")),
	SMUGGLERS		(3, SEASIDE, 0, new CardType[] { ACTION }, new Actions("Gain a copy of a card costing up to 6 Coins that the player to your right gained on his last turn.")),
	WAREHOUSE		(3, SEASIDE, 0, new CardType[] { ACTION }, new Actions(3, 1, 0, 0, "Discard 3 cards.")),

	ALCHEMIST			(3, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions(2, 1, 0, 0, "When you discard this from play, you may put this on top of your deck if you have a Potion in play.")),
	FAMILIAR			(3, true, ALCHEMY, 0, new CardType[] { ACTION, ATTACK }, new Actions(1, 1, 0, 0, "Each other player gains a curse.")),
	PHILOSOPHERS_STONE	(3, true, ALCHEMY, 0, new CardType[] { ACTION, TREASURE }, new Actions("When you play this, count your deck and discard pile. Worth 1 Coin per 5 cards total between them (rounded down).")),
	
	LOAN		(3, PROSPERITY, 0, new CardType[] { ACTION, TREASURE }, new Actions(1, 0, 0, 0, "When you play this, reveal cards from your deck until you reveal a Treasure. Discard it or trash it. Discard the other cards.")),
	TRADE_ROUTE	(3, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(0, 0, 0, 1, "+1 Coin per token on the Trade Route mat. Trash a card from your hand.\nSetup: Put a token on each Victory card Supply pile. When a card is gained from that pile, move the token to the Trade Route mat.")),
	WATCHTOWER	(3, PROSPERITY, 0, new CardType[] { ACTION, REACTION }, new Actions("Draw until you have 6 cards in hand. When you gain a card, you may reveal this from your hand. If you do, either trash that card or put it on top of your deck.")),
	
	BEAUROCRAT	(4, BASE, 0, new CardType[] { ACTION, ATTACK }, new Actions("Gain a silver card; put it on top of your deck. Each other player reveals a Victory card from his hand and puts it on his deck (or reveals a hand with no victory cards).")),
	FEAST		(4, BASE, 0, new CardType[] { ACTION }, new Actions("Trash this card. Gain a card costing up to 5.")),
	GARDENS		(4, BASE, 0, new CardType[] { ACTION, VICTORY }, new Actions("Worth 1VP for every 10 cards in your deck (rounded down).")),
	MILITIA		(4, BASE, 0, new CardType[] { ACTION, ATTACK }, new Actions(0, 0, 2, 0, "Each other player discards down to 3 cards in his hand.")),
	MONEYLANDER	(4, BASE, 0, new CardType[] { ACTION }, new Actions("Trash a copper card from your hand. If you do, +3 Coins.")),
	REMODEL 	(4, BASE, 0, new CardType[] { ACTION }, new Actions("Trash a card from your hand. Gain a card costing up to 2 coins more than the trashed card")),
	SMITHY		(4, BASE, 0, new CardType[] { ACTION }, new Actions(3, 0, 0, 0)),
	SPY			(4, BASE, 0, new CardType[] { ACTION, ATTACK }, new Actions(1, 1, 0, 0, "Each player (including you) reveals the top card of his deck and either discards it or puts it back, your choice.")),
	THIEF		(4, BASE, 0, new CardType[] { ACTION, ATTACK }, new Actions("Each other player reveals the top 2 cards of his deck. If they revealed any Treasure cards, they trash one of them that you choose. You may gain any or all of these trashed cards. They discard the other revealed cards.")),
	THRONE_ROOM	(4, BASE, 0, new CardType[] { ACTION }, new Actions("Choose an action card in your hand. Play it twice.")),
	
	BARON			(4, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(0, 0, 0, 1, "You may discard an Estate card. If you do, +4 coins. Otherwise, gain an Estate card.")),
	BRIDGE			(4, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(0, 0, 1, 1, "All cards (including cards in players' hands) cost 1 coin less this turn, but not less than 0 coins.")),
	CONSPIRATOR		(4, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(0, 0, 2, 0, "If you've played 3 or more Action cards this turn (counting this): +1 Card, +1 Action.")),
	COPPERSMITH		(4, INTRIGUE, 0, new CardType[] { ACTION }, new Actions("Copper produce an extra 1 coin this turn.")),
	IRONWORKS		(4, INTRIGUE, 0, new CardType[] { ACTION }, new Actions("Gain a card costing up to 4 coins.\nIf it's an...\nAction card, +1 Action\nTreasure card, +1 Coin\nVictory cards, +1 Card.")),
	MINING_VILLAGE	(4, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(1, 2, 0, 0, "You may trash this card immediately. If you do, +2 Coins.")),
	SCOUT			(4, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(0, 1, 0, 0, "Reveal the top 4 cards of your deck. Put the revealed Victory cards into your hand. Put the other cards on top of your deck in any order.")),

	CARAVAN		(4, SEASIDE, 0, new CardType[] { ACTION, DURATION }, new Actions(1, 1, 0, 0, "At the start of your next turn, +1 Card.")),
	CUTPURSE	(4, SEASIDE, 0, new CardType[] { ACTION, ATTACK }, new Actions(0, 0, 2, 0, "Each other player discards a Copper card (or reveals a hand with no Copper).")),
	ISLAND		(4, SEASIDE, 2, new CardType[] { ACTION, VICTORY }, new Actions("Set aside this and another card from your hand. Return them to your deck at the end of the game.")),
	NAVIGATOR	(4, SEASIDE, 0, new CardType[] { ACTION }, new Actions(0, 0, 2, 0, "Look at the top 5 Cards of your deck. Either discard all of them or put them back on top of your deck in any order.")),
	PIRATE_SHIP	(4, SEASIDE, 0, new CardType[] { ACTION }, new Actions("Choose one: Each other player reveals the top 2 cards of his deck, trashes a revealed Treasure that you choose, discards the rest, and if anyone trashed a Treasure you take a Coin token; or +1 Coin per Coin token you've taken with Pirate Ship this game.")),
	SALVAGER	(4, SEASIDE, 0, new CardType[] { ACTION }, new Actions(0, 0, 0, 1, "Trash a card from your hand. +Coins equal to its cost.")),
	SEA_HAG		(4, SEASIDE, 0, new CardType[] { ACTION, ATTACK }, new Actions("Each other player discards the top card of his deck, then gains a Curse card, putting it on top of his deck.")),
	TREASURE_MAP(4, SEASIDE, 0, new CardType[] { ACTION }, new Actions("Trash this and another copy of Treasure Map from your hand. If you do trash two Treasure Maps, gain 4 Gold cards, putting them on top of your deck.")),
	
	BISHOP			(4, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(0, 0, 1, 0, 1, "Trash a card from your hand. +<VP> equal to half its cost in coins, rounded down. Each other player may trash a card from his hand.")),
	MONUMENT		(4, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(0, 0, 2, 0, 1)),
	QUARRY			(4, PROSPERITY, 0, new CardType[] { ACTION, TREASURE }, new Actions(0, 0, 1, 0, "While this is in play, Action cards cost $2 less, but not less than $0.")),
	TALISMAN		(4, PROSPERITY, 0, new CardType[] { ACTION, TREASURE }, new Actions(0, 0, 1, 0, "While this is in play, when you buy a card costing $4 or less that is not a Victory card, gain a copy of it.")),
	WORKERS_VILLAGE	(4, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(2, 1, 0, 1)),
	
	GOLEM	(4, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions("Reveal cards from your deck until you reveal 2 Action cards other than Golem Cards. Discard the other cards, then play the Action cards in either order.")),
	
	COUNCIL_ROOM 	(5, BASE, 0, new CardType[] { ACTION }, new Actions(4, 0, 0, 1, "Each other player draws a card.")),
	FESTIVAL		(5, BASE, 0, new CardType[] { ACTION }, new Actions(0, 2, 2, 1)),
	LABORATORY		(5, BASE, 0, new CardType[] { ACTION }, new Actions(2, 1, 0, 0)),
	MARKET			(5, BASE, 0, new CardType[] { ACTION }, new Actions(1, 1, 1, 1)),
	LIBRARY			(5, BASE, 0, new CardType[] { ACTION }, new Actions("Draw until you have 7 cards in hand. You may set aside any action cards drawn this way, as you draw them; discard the set aside cards after you finish drawing.")),
	MINE			(5, BASE, 0, new CardType[] { ACTION }, new Actions("Trash a treasure card from your hand. Gain a treasure card costing up to 3 more; put it into your hand.")),
	WITCH			(5, BASE, 0, new CardType[] { ACTION, ATTACK }, new Actions(2, 0, 0, 0, "Each other payer gains a curse card.")),
	
	DUKE		(5, INTRIGUE, 0, new CardType[] { ACTION, VICTORY }, new Actions("Worth 1VP for every Duchy you have.")),
	MINION		(5, INTRIGUE, 0, new CardType[] { ACTION, ATTACK }, new Actions(0, 1, 0, 0, "Choose one: +2 Coins; or discard your hand, +4 Cards, and each other player with at least 5 cards in hand discards his hand and draws 4 cards.")),
	SABOTEUR	(5, INTRIGUE, 0, new CardType[] { ACTION, ATTACK }, new Actions("Each other player reveals cards from the top of his deck until revealing one costing 3 Coins or more. He trashes that card and may gain a card costing at most 2 Coins less than it. He discards the other revealed cards.")),
	TORTURER	(5, INTRIGUE, 0, new CardType[] { ACTION, ATTACK }, new Actions(3, 0, 0, 0, "Each other player choose one: he discards 2 cards; or he gains a Curse cards, putting it in his hand.")),
	TRADING_POST(5, INTRIGUE, 0, new CardType[] { ACTION }, new Actions("Trash 2 cards from your hand. If you do, gain a Silver card; put it into your hand.")),
	TRIBUTE		(5, INTRIGUE, 0, new CardType[] { ACTION }, new Actions("The player to your left reveals then discards the top 2 cards of his deck. For each differently named card revealed, if it is an...\nAction card, +2 Actions\nTreasure card, +2 Coins\nVictory cards, +2 Cards.")),
	UPGRADE		(5, INTRIGUE, 0, new CardType[] { ACTION }, new Actions(1, 1, 0, 0, "Trash a card from your hand. Gain a card costing exactly 1 Coin more than it.")),

	BAZAAR			(5, SEASIDE, 0, new CardType[] { ACTION }, new Actions(1, 2, 1, 0)),
	EXPLORER		(5, SEASIDE, 0, new CardType[] { ACTION }, new Actions("You may reveal a Province card from your hand. If you do, gain a Gold card, putting it into your hand. Otherwise, gain a Silver card, putting it into your hand.")),
	GHOST_SHIP		(5, SEASIDE, 0, new CardType[] { ACTION, ATTACK }, new Actions(2, 0, 0, 0, "Each other player with 4 or more in hand puts cards from his hand on top of his deck until he has 3 cards in his hand.")),
	MERCHANT_SHIP	(5, SEASIDE, 0, new CardType[] { ACTION, DURATION }, new Actions(0, 0, 2, 0, "Now and at the start of your next turn: +2 Coins.")),
	OUTPOST			(5, SEASIDE, 0, new CardType[] { ACTION, DURATION }, new Actions("You only draw 3 cards (instead of 5) in this turn's Clean-up phase. Take an extra turn after this one. This can't cause you to take more than two consecutive turns.")),
	TACTICIAL		(5, SEASIDE, 0, new CardType[] { ACTION }, new Actions("Discard your hand. If you discarded any cards this way, then at the start of your next turn, +5 Cards, +1 Buy, and +1 Action.")),
	TREASURY		(5, SEASIDE, 0, new CardType[] { ACTION }, new Actions(1, 1, 1, 0, "When you discard this from play, if you didn't buy a Victory card this turn, then you may put this on top of your deck.")),
	WHARF			(5, SEASIDE, 0, new CardType[] { ACTION }, new Actions(2, 0, 0, 1, "Now and at the start of your next turn +2 Cards +1 Buy.")),
	
	CITY			(5, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(1, 2, 0, 0, "If there are one or more empty Supply piles, +1 Card. If there are two or more, +$1 and +1 Buy.")),
	CONTRABAND		(5, PROSPERITY, 0, new CardType[] { ACTION, TREASURE }, new Actions(0, 0, 3, 1, "When you play this, the player to your left names a card. You can't buy that card this turn.")),
	COUNTING_HOUSE	(5, PROSPERITY, 0, new CardType[] { ACTION }, new Actions("Look through your discard pile, reveal any number of Copper cards from it, and put them into your hand.")),
	MINT			(5, PROSPERITY, 0, new CardType[] { ACTION }, new Actions("You may reveal a Treasure card from your hand. Gain a copy of it.\nWhen you buy this, trash all Treasures you have in play.")),
	MOUNTEBANK		(5, PROSPERITY, 0, new CardType[] { ACTION, ATTACK }, new Actions(0, 0, 2, 0, "Each other player may discard a Curse. If he doesn't, he gains a Curse and a Copper.")),
	RABBLE			(5, PROSPERITY, 0, new CardType[] { ACTION, ATTACK }, new Actions(3, 0, 0, 0, "Each other player reveals the top 3 cards of his deck, discards the revealed Actions and Treasures, and puts the rest back on top in any order he chooses.")),
	ROYAL_SEAL		(5, PROSPERITY, 0, new CardType[] { ACTION, TREASURE }, new Actions(0, 0, 2, 0, "While this is in play, when you gain a card, you may put that card on top of your deck.")),
	VAULT			(5, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(2, 0, 0, 0, "Discard any number of cards. +$1 per card discarded. \nEach other player may discard 2 cards. If he does, he draws a card.")),
	VENTURE			(5, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(0, 0, 1, 0, "When you play this, reveal cards from your deck until you reveal a Treasure. Discard the other cards. Play that Treasure.")),
	
	ADVENTURER	(6, BASE, 0, new CardType[] { ACTION }, new Actions("Reveal cards from  your deck until you reveal 2 Treasure cards. Put those treasure cards into your hand and discard the other revealed cards.")),

	HARLEM		(6, INTRIGUE, 2, new CardType[] { ACTION, TREASURE, VICTORY }, new Actions(0, 0, 2, 0)),
	NOBLES		(6, INTRIGUE, 2, new CardType[] { ACTION, VICTORY }, new Actions("Choose one: +3 Cards; or +2 Actions.")),
	
	GOONS		(6, PROSPERITY, 0, new CardType[] { ACTION, ATTACK }, new Actions(0, 0, 2, 1, "Each other player discards down to 3 cards in hand.\nWhile this is in play, when you buy a card, +1 <VP>.")),
	GRAND_MARKET(6, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(1, 1, 2, 1, "You can't buy this if you have any Copper in play.")),
	HOARD		(6, PROSPERITY, 0, new CardType[] { ACTION, TREASURE }, new Actions(0, 0, 2, 0, "While this is in play, when you buy a Victory card, gain a Gold.")),
	
	APPRENTICE	(6, false, ALCHEMY, 0, new CardType[] { ACTION }, new Actions(0, 1, 0, 0, "Trash a card from your hand. +1 Card per Coin it costs. +2 Cards if it has Potion in its cost.")),
	
	BANK		(7, PROSPERITY, 0, new CardType[] { ACTION, TREASURE}, new Actions("When you play this, it's worth 1 Coin per Treasure card you have in play (counting this).")),
	EXPAND		(7, PROSPERITY, 0, new CardType[] { ACTION }, new Actions("Trash a card from your hand. Gain a card costing up to 3 Coins more than the trashed card.")),
	FORGE		(7, PROSPERITY, 0, new CardType[] { ACTION }, new Actions("Trash any number of cards from your hand. Gain a card with cost exactly equal to the total cost in coins of the trashed cards.")),
	KINGS_COURT	(7, PROSPERITY, 0, new CardType[] { ACTION }, new Actions("You may choose an Action card in your hand. Play it three times.")),
	
	POSSESION	(7, true, ALCHEMY, 0, new CardType[] { ACTION }, new Actions("The player to your left takes an extra turn after this one, in which you can see all cards he can and make all decisions for him. Any cards he would gain on that turn, you gain instead; any cards of his that are trashed are set aside and returned to his discard pile at end of turn.")),
	
	PEDDLER		(8, PROSPERITY, 0, new CardType[] { ACTION }, new Actions(1, 1, 1, 0, "During your Buy phase, this costs $2 less per Action card you have in play, but not less than $0.")),
	
	; //(4, BASE, 0, new CardType[] { ACTION }, new Actions("")),

	private final int cost, victoryPoints;
	private final boolean needPotion;
	private final SetType set;
	private final List<CardType> types;
	private final Actions actions;
	
	private Card(int cost, SetType set, int victoryPoints, CardType[] cardTypes, Actions actions) {
		this(cost, false, set, victoryPoints, cardTypes, actions); 
	}
	
	private Card(int cost, boolean needPotion, SetType set, int victoryPoints, CardType[] cardTypes, Actions actions) {
		// Only ACTION type cards can have REACTION, ATTACK, or DURATION.
		// TODO(ekon): I feel like this is a manifestation of bad design.
		List<CardType> types = Arrays.<CardType>asList(cardTypes);
		if ((types.contains(VICTORY) || types.contains(TREASURE))
				&& (types.contains(REACTION) || types.contains(DURATION) || types.contains(ATTACK))) {
			throw new GameException(GameException.Type.CODE_ISSUE, "Card " + name() + " has invalid types " + types.toString());
		}
		
		this.cost = cost;
		this.needPotion = needPotion;
		this.set = set;
		this.victoryPoints = victoryPoints;
		this.types = types;
		this.actions = actions;
	}
	
	public int cost() { return cost; }
	public boolean needPostion() { return needPotion; }
	public SetType set() { return set; }
	public int victoryPoints() { return victoryPoints; }
	public Actions actions() { return actions; }
	public String description() { return actions.description(); }
	public List<CardType> types() { return types; }

	public boolean isProtection() { return (this == MOAT); }
	public boolean isDuration() { return types().contains(DURATION); }
	public boolean isAttack() { return types().contains(ATTACK); }
	
	@Override
	public String toString() {
		return name();
	}
	
	public enum CardType {
	  ACTION,
	  	ATTACK,
	  	DURATION,
	  	REACTION,
	  VICTORY,
	  TREASURE;
	}
	
	public enum SetType {
	  BASE,
	  INTRIGUE,
	  SEASIDE,
	  ALCHEMY,
	  PROSPERITY,
	  CORNUCOPIA,
	  HINDERLANDS;
	}
}
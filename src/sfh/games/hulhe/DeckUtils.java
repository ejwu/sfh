package sfh.games.hulhe;

import com.google.common.collect.Lists;

import org.pokersource.game.Deck;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeckUtils {

    public static final long FULL_DECK = createFullDeck();
    private static final Comparator<Long> CARD_COMPARATOR = getCardComparator();
    private static final Comparator<Long> HAND_COMPARATOR = getHandComparator();

    private static long createFullDeck() {
        long cardMask = 0;
        for (String rank : Lists.newArrayList(
            "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A")) {
            for (String suit : Lists.newArrayList("c", "d", "h", "s")) {
                cardMask |= Deck.parseCardMask(rank + suit);
            }
        }
        return cardMask;
    }

    /**
     * @param deadCards a bitmask representing the set of dead cards
     *
     * @return a list of every individual card still in the deck, in order
     */
    public static Iterable<Long> deckWithout(Long deadCards) {
        long remainingCards = FULL_DECK & ~deadCards;

        List<Long> cards = Lists.newArrayList();

        // This is dependent on the implementation of Deck using the first 52 bits
        long card = 1L;
        for (int i = 0; i < 52; i++) {
            if ((card & remainingCards) != 0) {
                cards.add(card);
            }
            card <<= 1;
        }

        Collections.sort(cards, getCardComparator());

        return cards;
    }

    /**
     * Get a comparator that sorts cards by rank, then suit
     */
    public static Comparator<Long> getCardComparator() {
        if (CARD_COMPARATOR != null) {
            return CARD_COMPARATOR;
        } else {
            return new Comparator<Long>() {
                public int compare(Long first, Long second) {
                    // Try the first card in each hand
                    int rankDiff = Deck.parseRank(Deck.cardMaskString(second).substring(0, 1)) -
                        Deck.parseRank(Deck.cardMaskString(first).substring(0, 1));
                    if (rankDiff != 0) {
                        return rankDiff;
                    }
                
                    // Both cards same rank, use suit instead
                    return Deck.parseSuit(Deck.cardMaskString(second).substring(1, 2)) -
                        Deck.parseSuit(Deck.cardMaskString(first).substring(1, 2));
                }
            };
        }
    }

    /**
     * Get a comparator that returns two card hands in order based on rank of the first card,
     * then rank of the second card.  It's pretty ugly.
     */
    public static Comparator<Long> getHandComparator() {
        return new Comparator<Long>() {
            public int compare(Long first, Long second) {
                // Try the first card in each hand
                int rankDiff =
                    Deck.parseRank(Deck.cardMaskString(second).split(" ")[0].substring(0, 1)) -
                    Deck.parseRank(Deck.cardMaskString(first).split(" ")[0].substring(0, 1));
                if (rankDiff != 0) {
                    return rankDiff;
                }

                // Try the second card in each hand.  Since cards within hands are sorted, this
                // makes pairs come before unpaired hands
                int secondRankDiff =
                    Deck.parseRank(Deck.cardMaskString(second).split(" ")[1].substring(0, 1)) -
                    Deck.parseRank(Deck.cardMaskString(first).split(" ")[1].substring(0, 1));
                if (secondRankDiff != 0) {
                    return secondRankDiff;
                }

                // Both cards same, check suit of first card
                int suitDiff =
                    Deck.parseSuit(Deck.cardMaskString(second).split(" ")[0].substring(1, 2)) -
                    Deck.parseSuit(Deck.cardMaskString(first).split(" ")[0].substring(1, 2));
                if (suitDiff != 0) {
                    return suitDiff;
                }

                // Finally, check suit of second card
                return Deck.parseSuit(Deck.cardMaskString(second).split(" ")[1].substring(1, 2)) -
                    Deck.parseSuit(Deck.cardMaskString(first).split(" ")[1].substring(1, 2));
            }
        };
    }

}

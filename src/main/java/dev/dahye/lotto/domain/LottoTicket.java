package dev.dahye.lotto.domain;

import java.util.List;

import static java.util.Collections.shuffle;
import static java.util.Collections.sort;

public class LottoTicket {
    private static final int LOTTO_TICKET_NUMBER_MIN_SIZE = 0;
    private static final int LOTTO_TICKET_NUMBER_MAX_SIZE = 6;
    private final List<Integer> lottoNumbers;

    public LottoTicket(List<Integer> lottoNumbers) {
        this.lottoNumbers = lottoNumbers;
    }

    public static LottoTicket issued() {
        return new LottoTicket(createLottoNumbers());
    }

    public List<Integer> getLottoNumbers() {
        return lottoNumbers;
    }

    private static List<Integer> createLottoNumbers() {
        List<Integer> numbers = LottoNumber.getNumbers();
        shuffle(numbers);
        sort(numbers);

        return numbers.subList(LOTTO_TICKET_NUMBER_MIN_SIZE, LOTTO_TICKET_NUMBER_MAX_SIZE);
    }

    public boolean validateLottoNumberSize() {
        return lottoNumbers.size() == LOTTO_TICKET_NUMBER_MAX_SIZE;
    }

    public int getMatchCount(List<Integer> winningNumbers) {
        int matchCount = 0;

        for (Integer winningNumber : winningNumbers) {
            matchCount = getMatchCount(matchCount, winningNumber);
        }

        return matchCount;
    }

    private int getMatchCount(int matchCount, Integer winningNumber) {
        if (lottoNumbers.contains(winningNumber)) {
            matchCount++;
        }
        return matchCount;
    }
}

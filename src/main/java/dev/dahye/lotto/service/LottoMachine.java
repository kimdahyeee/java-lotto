package dev.dahye.lotto.service;

import dev.dahye.lotto.domain.LottoTicket;
import dev.dahye.lotto.domain.Money;
import dev.dahye.lotto.domain.Winning;
import dev.dahye.lotto.util.DoubleUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LottoMachine {
    private static final int LOTTO_PRICE = 1000;
    private static final String WINNERS_DELIMITER = ",";

    private List<LottoTicket> lottoTickets;
    private LottoTicket winningTicket;
    private final Money money;

    public LottoMachine(int money) {
        this.money = new Money(money);

        initializeTickets();

        int lottoCount = money / LOTTO_PRICE;
        for (int i = 0; i < lottoCount; i++) {
            lottoTickets.add(LottoTicket.autoIssued());
        }
    }

    private void initializeTickets() {
        this.lottoTickets = new ArrayList<>();
    }

    public LottoMachine(int money, List<LottoTicket> lottoTickets) {
        this.money = new Money(money);
        this.lottoTickets = lottoTickets;
    }

    public List<LottoTicket> getLottoTickets() {
        return Collections.unmodifiableList(lottoTickets);
    }

    protected int getTicketsCount() {
        return lottoTickets.size();
    }

    public List<Winning> getWinnings(String winningNumberInput) {
        validateWinnersNullOrEmpty(winningNumberInput);
        List<Integer> winningNumbers = convertStringToIntegerList(winningNumberInput);
        setWinningTicket(winningNumbers);

        List<Winning> winnings = new ArrayList<>();
        for (LottoTicket lottoTicket : lottoTickets) {
            int matchCount = lottoTicket.getMatchCount(winningTicket);
            addWhenIsWinning(winnings, matchCount);
        }

        return winnings;
    }

    private void setWinningTicket(List<Integer> winningNumbers) {
        validateAlreadyAssignedWinningTicket();
        this.winningTicket = LottoTicket.manualIssued(winningNumbers);
    }

    private void validateAlreadyAssignedWinningTicket() {
        if (this.winningTicket != null) {
            throw new RuntimeException("이미 할당된 당첨 번호가 존재합니다.");
        }
    }

    private void addWhenIsWinning(List<Winning> winnings, int matchCount) {
        if (Winning.isWinning(matchCount)) {
            winnings.add(Winning.getWinning(matchCount));
        }
    }

    private void validateWinnersNullOrEmpty(String winningNumberInput) {
        if (winningNumberInput == null || winningNumberInput.trim().isEmpty()) {
            throw new IllegalArgumentException("당첨 번호는 반드시 입력되어야 합니다.");
        }
    }

    private List<Integer> convertStringToIntegerList(String input) {
        String[] strings = input.split(WINNERS_DELIMITER);
        List<Integer> ints = new ArrayList<>();

        for (String string : strings) {
            ints.add(convertStringToInteger(string.trim()));
        }

        return ints;
    }

    private int convertStringToInteger(String string) {
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("당첨 번호는 숫자만 입력 가능합니다.");
        }
    }

    public double getWinningRate(List<Winning> winnings) {
        int totalPrize = 0;
        for (Winning winning : winnings) {
            totalPrize += winning.getPrize();
        }

        return DoubleUtils.parseDoubleSecondDigit(totalPrize / money.getMoney());
    }
}

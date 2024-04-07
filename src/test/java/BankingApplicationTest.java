import domain.CardModel;
import domain.CheckingAccountModel;
import domain.CurrencyType;
import domain.MoneyModel;
import org.junit.Test;
import seed.SeedInitializer;
import services.TransactionManagerService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static seed.CardsSeedData.card1;
import static seed.CardsSeedData.card2;

public class BankingApplicationTest {

    @Test
    public void expectTrueTest() {
        assertTrue(true);
    }
}

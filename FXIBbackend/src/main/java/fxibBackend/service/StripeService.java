package fxibBackend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import fxibBackend.dto.UserDetailsDTO.StripeTransactionDTO;
import fxibBackend.exception.ResourceNotFoundException;
import fxibBackend.repository.TransactionEntityRepository;
import fxibBackend.util.CustomDateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fxibBackend.constants.ConfigConst.STRIPE_API_KEY;
import static fxibBackend.constants.OtherConst.*;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final TransactionEntityRepository transactionEntityRepository;
    private final CustomDateFormatter customDateFormatter;


    /**
     * Retrieves a list of Stripe transactions associated with a given email.
     *
     * @param email The email for which to fetch transactions.
     * @return A list of Stripe transaction DTOs.
     * @throws StripeException           If there's an issue with the Stripe API.
     * @throws ResourceNotFoundException If no transactions are found for the given email.
     */
    public List<StripeTransactionDTO> getAllTransactionsFromEmail(String email) throws StripeException {
        List<StripeTransactionDTO> transactionDTOS = new ArrayList<>();
        Stripe.apiKey = STRIPE_API_KEY;
        Map<String, Object> params = new HashMap<>();
        ChargeCollection charges = Charge.list(params);
        if (charges.getData().isEmpty()) {
            throw new ResourceNotFoundException();
        }
        for (Charge charge : charges.getData()) {
            if (charge.getBillingDetails().getEmail().equals(email)) {
                LocalDateTime billingDate = customDateFormatter.timeStampToDate(charge.getCreated());
                String amount = charge.getAmount().toString();
                String duration = transformToDuration(amount);
                LocalDateTime endOfBillingDate = billingDate.plusMonths(Long.parseLong(duration.split(" ")[0]));
                String currency = charge.getCurrency();
                String card = charge.getPaymentMethodDetails().getCard().getBrand() + " " + charge.getPaymentMethodDetails().getCard().getLast4();
                String amountAsText = (Integer.parseInt(amount) / 100) + " " + currency.toUpperCase();

                StripeTransactionDTO stripeTransactionDTO = new StripeTransactionDTO();
                stripeTransactionDTO.setUserEmail(email);
                stripeTransactionDTO.setBillingDate(customDateFormatter.formatLocalDateTimeNowAsString(billingDate));
                stripeTransactionDTO.setDuration(duration);
                stripeTransactionDTO.setEndOfBillingDate(customDateFormatter.formatLocalDateTimeNowAsString(endOfBillingDate));
                stripeTransactionDTO.setAmount(amountAsText);
                stripeTransactionDTO.setCard(card);
                stripeTransactionDTO.setStatus(charge.getStatus());
                stripeTransactionDTO.setReceipt(charge.getReceiptUrl());
                stripeTransactionDTO.setDescription(charge.getCalculatedStatementDescriptor());

                if (!transactionEntityRepository
                        .existsByAmountAndBillingDateAndCardAndDurationAndEndOfBillingDateAndUserEmail(
                                stripeTransactionDTO.getAmount(),
                                stripeTransactionDTO.getBillingDate(),
                                stripeTransactionDTO.getCard(),
                                stripeTransactionDTO.getDuration(),
                                stripeTransactionDTO.getEndOfBillingDate(),
                                stripeTransactionDTO.getUserEmail()
                        )) {
                    transactionDTOS.add(stripeTransactionDTO);
                }
            }
        }
        return transactionDTOS;
    }




    /**
     * Transforms the transaction amount to a duration string based on predefined price constants.
     *
     * @param amount The transaction amount.
     * @return The corresponding duration string.
     */
    public String transformToDuration(String amount) {
        return switch (amount) {
            case PLAN_PRICE_ONE -> PLAN_DURATION_ONE;
            case PLAN_PRICE_TWO -> PLAN_DURATION_TWO;
            case PLAN_PRICE_THREE -> PLAN_DURATION_THREE;
            default -> PLAN_PRICE_DURATION;
        };
    }

}

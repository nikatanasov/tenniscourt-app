package app.transaction.service;

import app.product.model.Product;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.repository.TransactionRepository;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public Transaction createTransaction(BigDecimal amount, TransactionType transactionType, TransactionStatus transactionStatus, String description, String failureReason, LocalDateTime createdOn, User owner, List<Product> products) {
        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(transactionType)
                .status(transactionStatus)
                .description(description)
                .failureReason(failureReason)
                .createdOn(createdOn)
                .owner(owner)
                .products(products)
                .build();

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsForUser(User user) {
        return transactionRepository.findAllByOwnerIdOrderByCreatedOnDesc(user.getId());
    }
}

package echipa13.calatorii.repository;

import echipa13.calatorii.models.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWallet_IdOrderBySpentAtDescIdDesc(Long walletId);

    @Query("select coalesce(sum(t.amount), 0) from WalletTransaction t where t.wallet.id = :walletId")
    BigDecimal sumAmountByWalletId(@Param("walletId") Long walletId);

    @Query("""
           select t.category, coalesce(sum(t.amount), 0)
           from WalletTransaction t
           where t.wallet.id = :walletId
           group by t.category
           order by coalesce(sum(t.amount), 0) desc
           """)
    List<Object[]> sumByCategory(@Param("walletId") Long walletId);

    @Query("select count(distinct t.spentAt) from WalletTransaction t where t.wallet.id = :walletId")
    long countDistinctSpentDays(@Param("walletId") Long walletId);
}

package com.saf.restaurant.actors;

import com.saf.core1.Actor;
import com.saf.core1.ActorContext;
import com.saf.core1.Message;
import com.saf.restaurant.entity.TreasuryEntryEntity;
import com.saf.restaurant.repository.TreasuryRepository;

import java.math.BigDecimal;
import java.time.Instant;

public class TreasuryActor implements Actor {

    private final TreasuryRepository treasuryRepository;

    public TreasuryActor(TreasuryRepository treasuryRepository) {
        this.treasuryRepository = treasuryRepository;
    }

    @Override
    public void onReceive(ActorContext ctx, Message msg) {
        if (msg instanceof RestaurantMessages.RecordPayment recordPayment) {
            recordPayment(recordPayment);
        } else if (msg instanceof RestaurantMessages.TreasurySnapshotRequest request) {
            BigDecimal total = treasuryRepository.totalRevenue();
            if (total == null) {
                total = BigDecimal.ZERO;
            }
            long count = treasuryRepository.ordersSettled();
            request.replyTo().tell(new RestaurantMessages.TreasurySnapshot(total, Math.toIntExact(count)));
        }
    }

    private void recordPayment(RestaurantMessages.RecordPayment recordPayment) {
        if (recordPayment.amount() == null) {
            return;
        }
        if (treasuryRepository.findByOrderId(recordPayment.orderId()).isPresent()) {
            return;
        }
        TreasuryEntryEntity entry = new TreasuryEntryEntity();
        entry.setOrderId(recordPayment.orderId());
        entry.setAmount(recordPayment.amount());
        entry.setRecordedAt(Instant.now());
        treasuryRepository.save(entry);
    }
}

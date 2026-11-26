package com.ore.backend.ownershipTransfer.entity;

import com.ore.backend.product.entity.ProductEntity;
import com.ore.backend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ownership_transfers")
public class OwnershipTransferEntity {

    public static final String NOTIFICATION_REFERENCE_TYPE = "OWNERSHIP_TRANSFER";

    @Id
    @GeneratedValue
    @Column(name = "transfer_id", nullable = false, updatable = false)
    private UUID transferId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserEntity requesterUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id", nullable = false)
    private UserEntity recipientUser;

    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OwnershipTransferStatus status;

    @Column(name = "privacy_agreed_at")
    private LocalDateTime privacyAgreedAt;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "transferred_at")
    private LocalDateTime transferredAt;

    @Builder
    public OwnershipTransferEntity(
            ProductEntity product,
            UserEntity requesterUser,
            UserEntity recipientUser,
            String recipientEmail
    ) {
        this.product = product;
        this.requesterUser = requesterUser;
        this.recipientUser = recipientUser;
        this.recipientEmail = recipientEmail;
        this.status = OwnershipTransferStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        this.requestedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = OwnershipTransferStatus.PENDING;
        }
    }

    public void respond(OwnershipTransferStatus status, boolean privacyAgreed) {
        this.status = status;
        this.respondedAt = LocalDateTime.now();

        if (status == OwnershipTransferStatus.ACCEPTED) {
            this.privacyAgreedAt = privacyAgreed ? this.respondedAt : null;
            this.transferredAt = this.respondedAt;
        }
    }
}

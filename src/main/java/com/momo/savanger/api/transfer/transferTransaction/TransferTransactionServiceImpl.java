package com.momo.savanger.api.transfer.transferTransaction;

import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.dto.TransferTransactionPair;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferMapper;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.dto.TransferDto;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferTransactionServiceImpl implements TransferTransactionService {

    private final TransferTransactionRepository transferTransactionRepository;

    private final TransactionService transactionService;

    private final TransferService transferService;

    private final TransferMapper transferMapper;

    @Override
    public TransferTransaction create(CreateTransferTransactionDto dto) {
        final TransferTransaction transferTransaction = new TransferTransaction();
        transferTransaction.setTransferId(dto.getTransferId());

        this.transferTransactionRepository.saveAndFlush(transferTransaction);

        final Transfer transfer = this.transferService.getById(dto.getTransferId());

        this.transactionService.createTransferTransactions(dto, transferTransaction.getId(),
                transfer);

        return transferTransaction;
    }

    @Override
    public TransferTransactionDto getTransferTransactionDto(Long transferId,
            Long transferTransactionId) {

        final TransferDto transferDto = this.transferMapper.toTransferDto(
                this.transferService.findAndFetchDetails(transferId)
        );

        final TransferTransactionPair transferTransactionPair = this.transactionService
                .getTransferTransactionPair(transferTransactionId);

        final TransferTransactionDto transferTransactionDto = new TransferTransactionDto();

        transferTransactionDto.setTransfer(transferDto);
        transferTransactionDto.setTransferTransactionId(transferTransactionId);
        transferTransactionDto.setReceiverTransaction(
                transferTransactionPair.getReceiverTransaction());
        transferTransactionDto.setSourceTransaction(
                transferTransactionPair.getSourceTransaction());

        return transferTransactionDto;
    }

    @Override
    public TransferTransaction getTransferTransaction(Long transferTransactionId) {
        return this.transferTransactionRepository.findById(transferTransactionId)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0019)
                );
    }

    @Override
    @Transactional
    public void revertTransferTransaction(Long transferTransactionId) {

        final TransferTransactionPair transferTransactionPair = this.transactionService.getTransferTransactionPair(
                transferTransactionId);

        this.transactionService.deleteById(
                transferTransactionPair.getReceiverTransaction().getId());
        this.transactionService.deleteById(transferTransactionPair.getSourceTransaction().getId());

        this.transferTransactionRepository.deleteById(transferTransactionId);
    }
}

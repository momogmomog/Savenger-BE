package com.momo.savanger.api.transfer.transferTransaction;

import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.dto.TransferTransactionPair;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferDto;
import com.momo.savanger.api.transfer.TransferMapper;
import com.momo.savanger.api.transfer.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        this.transactionService.createTransferTransactions(
                dto, transferTransaction.getId(), transfer);

        return transferTransaction;
    }

    @Override
    public TransferTransactionDto getTransferTransactionDto(Long transferId,
            Long transferTransactionId) {

        TransferDto transferDto = this.transferMapper.toTransferDto(
                this.transferService.findAndFetchDetails(transferId)
        );

        TransferTransactionPair transferTransactionPair = this.transactionService.getTransferTransactionPair(
                transferTransactionId);

        TransferTransactionDto transferTransactionDto = new TransferTransactionDto();
        transferTransactionDto.setTransferDto(transferDto);
        transferTransactionDto.setTransferTransactionId(transferTransactionId);
        transferTransactionDto.setReceiverTransactionDto(
                transferTransactionPair.getReceiverTransaction());
        transferTransactionDto.setSourceTransactionDto(
                transferTransactionPair.getSourceTransaction());

        return transferTransactionDto;
    }
}

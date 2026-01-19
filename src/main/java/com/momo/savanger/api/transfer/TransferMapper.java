package com.momo.savanger.api.transfer;

import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    Transfer toTransfer(CreateTransferDto createTransferDto);

    TransferDto toTransferDto(Transfer transfer);
}

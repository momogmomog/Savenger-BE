package com.momo.savanger.api.transfer;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    Transfer toTransfer(CreateTransferDto createTransferDto);

    TransferDto toTransferDto(Transfer transfer);
}

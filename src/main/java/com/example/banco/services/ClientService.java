package com.example.banco.services;

import com.example.banco.repositories.AccountRepository;
import com.example.banco.models.AccountStatus;
import com.example.banco.exceptions.BusinessException;
import com.example.banco.models.Client;
import com.example.banco.dtos.ClientInput;
import com.example.banco.repositories.ClientRepository;
import com.example.banco.dtos.ClientView;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

@Service
@Transactional
public class ClientService {
    private final ClientRepository clients;
    private final AccountRepository accounts;

    public ClientService(ClientRepository clientRepository, AccountRepository accountRepository) {
        clients = clientRepository;
        accounts = accountRepository;
    }

    public ClientView create(ClientInput input) {
        Client c = new Client();
        build(c, input);
        return ClientView.of(clients.save(c));
    }

    @Transactional(readOnly = true)
    public ClientView get(Long id) {
        return ClientView.of(clients.findById(id).orElseThrow(BusinessException::missing));
    }

    @Transactional(readOnly = true)
    public Page<ClientView> list(Pageable p) {
        return clients.findAll(p).map(ClientView::of);
    }

    public ClientView update(Long id, ClientInput input) {
        Client c = findById(id);
        if (!c.getDocument().equals(input.document()) && accounts.existsByclientId(id))
            throw BusinessException.conflict("DOCUMENT_IN_USE", "CPF/CNPJ não pode mudar após abertura de conta");
        build(c, input);
        return ClientView.of(c);
    }

    public void delete(Long id) {
        Client c = findById(id);
        if (accounts.existsByclientIdAndStatus(id, AccountStatus.ACTIVE))
            throw BusinessException.conflict("ACTIVE_ACCOUNTS", "Encerre as contas do cliente primeiro");
        c.setActive(false);
    }

    private Client findById(Long id) {
        return clients.findById(id).orElseThrow(BusinessException::missing);
    }

    private void build(Client c, ClientInput i) {
        c.setName(i.name());
        c.setDocument(i.document());
        c.setEmail(i.email());
    }
}

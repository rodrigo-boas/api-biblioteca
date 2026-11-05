package exercicio.api.service;

import exercicio.api.dto.MembroRequest;
import exercicio.api.dto.MembroResponse;
import exercicio.api.exceptions.CollisionArgumentException;
import exercicio.api.exceptions.MembroNotFoundException;
import exercicio.api.mapper.MembroMapper;
import exercicio.api.model.Membro;
import exercicio.api.repository.MembroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MembroService {

    private final MembroMapper mapper;
    private final MembroRepository repository;

    @Transactional
    public void cadastrar(MembroRequest membro) {
        Membro cadastro = mapper.toMembro(membro);
        if (!repository.existsByEmail(cadastro.getEmail())) repository.save(cadastro);
        else throw new CollisionArgumentException("O email já foi usado");
    }

    @Transactional
    public void cadastrarTodos(List<MembroRequest> membros) {
        List<Membro> list = membros.stream().map(mapper::toMembro).toList();

        List<String> emails = list.stream().map(Membro::getEmail).toList();
        long distinct = emails.stream().distinct().count();

        if (distinct != emails.size()) throw new CollisionArgumentException("Emails duplicados na lista");
        else if (repository.existsByEmailIn(emails)) throw new CollisionArgumentException("Email já existe");
        repository.saveAll(list);
    }

    @Transactional(readOnly = true)
    public List<MembroResponse> listAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MembroResponse findById(Long id) {
        Membro membro = repository.findById(id).orElseThrow(MembroNotFoundException::new);
        return mapper.toResponse(membro);
    }

    @Transactional
    public void atualizar(Long id, MembroRequest membroRequest) {
        Membro membro = repository.findById(id).orElseThrow(MembroNotFoundException::new);

        if (repository.existsByEmailAndIdNot(membroRequest.getEmail(), id)) {
            throw new CollisionArgumentException("O email já foi usado");
        }

        Membro membroFinal = mapper.toMembro(membroRequest);

        membro.setNome(membroFinal.getNome());
        membro.setEmail(membroFinal.getEmail());
        membro.setSenha(membroFinal.getSenha());
    }

    @Transactional
    public void delete(Long id) {
        repository.findById(id).orElseThrow(MembroNotFoundException::new);
        repository.deleteById(id);
    }
}

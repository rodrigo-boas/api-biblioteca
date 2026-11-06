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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MembroService {

    private final MembroMapper mapper;
    private final MembroRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void cadastrar(MembroRequest membro) {
        Membro cadastro = mapper.toMembro(membro);
        if (!repository.existsByEmail(cadastro.getEmail())) {
            cadastro.setSenha(passwordEncoder.encode(cadastro.getSenha()));
            repository.save(cadastro);
        }
        else throw new CollisionArgumentException("O email já foi usado");
    }

    @Transactional
    public void cadastrarTodos(List<MembroRequest> membros) {
        List<Membro> list = membros.stream().map(mapper::toMembro).toList();

        List<String> emails = list.stream().map(Membro::getEmail).toList();
        long distinct = emails.stream().distinct().count();

        if (distinct != emails.size()) throw new CollisionArgumentException("Emails duplicados na lista");
        else if (repository.existsByEmailIn(emails)) throw new CollisionArgumentException("Email já existe");

        list.forEach(m -> m.setSenha(passwordEncoder.encode(m.getSenha())));
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

        Membro membroFinal = mapper.toMembro(membroRequest);

        if (repository.existsByEmailAndIdNot(membroFinal.getEmail(), id)) {
            throw new CollisionArgumentException("O email já foi usado");
        }

        membro.setNome(membroFinal.getNome());
        membro.setEmail(membroFinal.getEmail());
        membro.setSenha(passwordEncoder.encode(membroFinal.getSenha()));
    }

    @Transactional
    public void delete(Long id) {
        repository.findById(id).orElseThrow(MembroNotFoundException::new);
        repository.deleteById(id);
    }
}

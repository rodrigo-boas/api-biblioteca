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
    private final MembroRepository membroRepository;

    @Transactional
    public void cadastrar(MembroRequest membro) {
        Membro cadastro = mapper.toMembro(membro);
        if (!membroRepository.existsByEmail(cadastro.getEmail())) membroRepository.save(cadastro);
        else throw new CollisionArgumentException("O email já foi usado");
    }

    @Transactional(readOnly = true)
    public List<MembroResponse> listAll() {
        return membroRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MembroResponse findById(Long id) {
        Membro membro = membroRepository.findById(id).orElseThrow(MembroNotFoundException::new);
        return mapper.toResponse(membro);
    }

    @Transactional
    public void atualizar(Long id, MembroRequest membroRequest) {
        Membro membro = membroRepository.findById(id).orElseThrow(MembroNotFoundException::new);

        if (membroRepository.existsByEmailAndIdNot(membroRequest.getEmail(), id)) {
            throw new CollisionArgumentException("O email já foi usado");
        }

        membro.setNome(membroRequest.getNome());
        membro.setEmail(membroRequest.getEmail());
        membro.setSenha(membroRequest.getSenha());
    }

    @Transactional
    public void delete(Long id) {
        membroRepository.findById(id).orElseThrow(MembroNotFoundException::new);
        membroRepository.deleteById(id);
    }
}

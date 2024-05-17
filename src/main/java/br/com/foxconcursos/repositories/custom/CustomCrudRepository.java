package br.com.foxconcursos.repositories.custom;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.QueryByExampleExecutor;

@NoRepositoryBean
public interface CustomCrudRepository<T, ID> extends ListCrudRepository<T, ID>, QueryByExampleExecutor<T>  {
    
}

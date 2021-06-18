import createRepository from '@/infrastructure/UnitOfWork'

export default (ctx, inject) => {
  inject('uow', createRepository(ctx.$axios))
}

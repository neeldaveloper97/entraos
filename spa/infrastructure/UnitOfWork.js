import templateApi from '@/infrastructure/template/templateApi';
import companyApi from '@/infrastructure/company/companyApi';
import contractApi from '@/infrastructure/contract/contractApi';
export default ($axios) => ({
  template: templateApi($axios),
  company: companyApi($axios),
  contract: contractApi($axios)
})

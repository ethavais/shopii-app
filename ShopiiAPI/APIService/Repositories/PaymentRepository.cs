using APIService.Data;
using APIService.Models;

namespace APIService.Repositories
{
    public class PaymentRepository : GenericRepository<Payment>, IPaymentRepository
    {
        public PaymentRepository(ShopiiDbContext context) : base(context)
        {
        }
    }
    public interface IPaymentRepository : IGenericRepository<Payment>
    {

    }
}

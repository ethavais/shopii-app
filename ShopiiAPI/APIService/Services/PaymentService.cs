using APIService.Models;
using Newtonsoft.Json;
using static APIService.Models.Payment;
using System.Net.Http.Headers;
using System.Security.Cryptography;
using System.Text;
using APIService.Repositories;
using Net.payOS;
using Net.payOS.Types;
using static APIService.Services.PaymentService;

namespace APIService.Services
{
    public class PaymentService : IPaymentService
    {
        private readonly IHttpContextAccessor _contextAccessor;
        private readonly IPaymentRepository _repo;
        private readonly PayOS _payOS;


        public PaymentService(PayOS payOS, IHttpContextAccessor httpContextAccessor, IConfiguration configuration)
        {
            _payOS = payOS;
            _contextAccessor = httpContextAccessor;
        }

        public async Task<IEnumerable<Payment>> GetAllAsync()
        {
            var result = await _repo.GetAllAsync();
            if ( result.Count() > 0)
            {
                foreach (var item in result)
                {
                    PaymentLinkInformation paymentLinkInformation = await _payOS.getPaymentLinkInformation(item.OrderCode);
                    item.Status = paymentLinkInformation.status.ToString();
                    await _repo.UpdateAsync(item);
                }
            }
            return result.ToList();

        }

        public async Task<CreatePaymentResult> CheckOut(Guid userId, List<CardItem> cardItems)
        {

            // 2. Tạo PayOSPaymentRequest từ thông tin booking
            long orderCode = DateTimeOffset.Now.ToUnixTimeSeconds();
            List<ItemData> items = new List<ItemData>();
            var totalMoney = 0;
            foreach (var data in cardItems)
            {
                items.Add(new ItemData(data.ProductId.ToString(), data.Quantity, data.TotalPrice));
                totalMoney += data.TotalPrice;
            }
            var request = _contextAccessor.HttpContext.Request;
            var baseUrl = $"{request.Scheme}://{request.Host}";
            PaymentData paymentData = new PaymentData(
                                                     orderCode,
                                                     totalMoney,
                                                     "Thanh toan don hang",
                                                     items,
                                                     $"{baseUrl}/cancel",
                                                     $"{baseUrl}/success"
                                                 );
            CreatePaymentResult createPayment = await _payOS.createPaymentLink(paymentData);

            var payOSRequest = new Payment
            {
                  OrderCode = orderCode,
                    Amount = totalMoney,
                     UserId = userId
            };
             await _repo.AddAsync(payOSRequest);

            return createPayment;
        }
        public async Task<PaymentLinkInformation> GetOrder(long orderId)
        {
            try
            {
                PaymentLinkInformation paymentLinkInformation = await _payOS.getPaymentLinkInformation(orderId);
                var result = await _repo.GetAllAsync();
                var existingUserPayment = result.FirstOrDefault(x => x.OrderCode == orderId);

                existingUserPayment.Status = paymentLinkInformation.status.ToString();
                await _repo.UpdateAsync(existingUserPayment);
                return paymentLinkInformation;
            }
            catch 
            {
                throw;
            }


        }
        // CancleOrder
        public async Task<PaymentLinkInformation> CancelOrder(long orderId)
        {
            try
            {
                PaymentLinkInformation paymentLinkInformation = await _payOS.cancelPaymentLink(orderId);

                var result = await _repo.GetAllAsync();
                var existingUserPayment = result.FirstOrDefault(x => x.OrderCode == orderId);
                existingUserPayment.Status = paymentLinkInformation.status;
                await _repo.UpdateAsync(existingUserPayment);

                return paymentLinkInformation;
            }
            catch
            {
                throw;
            }


        }
        public async Task<IEnumerable<Payment>> GetPaymentbyUserId(Guid id)
        {
            var result = await _repo.GetAllAsync();
            return result.Where(x => x.UserId == id).ToList();

        }
        public async Task<CreatePaymentResult> CreatePaymentLink(Guid userId, List<CardItem> cardItems, string returnUrl,string cancelUrl)
        {

                long orderCode = DateTimeOffset.Now.ToUnixTimeSeconds();

                List<ItemData> items = new List<ItemData>();
                var totalMoney = 0;
                foreach (var data in cardItems)
                {
                    items.Add(new ItemData(data.ProductId.ToString(), data.Quantity, data.TotalPrice));
                    totalMoney += data.TotalPrice;
                }
                PaymentData paymentData = new PaymentData(orderCode, totalMoney, "Thanh Toan Don Hang", items, cancelUrl, returnUrl);

                CreatePaymentResult createPayment = await _payOS.createPaymentLink(paymentData);
                var payOSRequest = new Payment
                {
                    Status = createPayment.status,
                    OrderCode = orderCode,
                    Amount = totalMoney,
                    UserId = userId
                };
                await _repo.AddAsync(payOSRequest);
                return createPayment;
            
        }
        public interface IPaymentService
        {
            Task<CreatePaymentResult> CreatePaymentLink(Guid userId, List<CardItem> cardItems, string returnUrl, string cancelUrl);
             Task<IEnumerable<Payment>> GetPaymentbyUserId(Guid id);
            Task<PaymentLinkInformation> GetOrder(long orderId);

            Task<PaymentLinkInformation> CancelOrder(long orderId);
            Task<CreatePaymentResult> CheckOut(Guid userId, List<CardItem> cardItems);
            Task<IEnumerable<Payment>> GetAllAsync();





        }

    }
}

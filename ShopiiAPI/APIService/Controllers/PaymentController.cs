using APIService.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Net.payOS.Types;
using static APIService.Services.PaymentService;

namespace APIService.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PaymentController : ControllerBase
    {
        private IPaymentService _service;
        public PaymentController(IPaymentService service)
        {
            _service = service;
        }
        [Authorize]
        [HttpPost("/create-payment-link")]
        public async Task<IActionResult> Checkout(Guid userId, List<CardItem> cardItems)
        {
            CreatePaymentResult result = await _service.CheckOut(userId,cardItems);
            return Ok();
        }
        [Authorize]
        [HttpPost("create")]

        public async Task<IActionResult> CreatePaymentLink(Guid userId, List<CardItem> cardItems, string returnUrl, string cancelUrl)
        {
            CreatePaymentResult result = await _service.CreatePaymentLink(userId,cardItems,returnUrl,cancelUrl);
            return Ok();

        }
        [Authorize]
        [HttpGet("get-order/{orderId}")]
        public async Task<IActionResult> GetOrder([FromRoute] int orderId)
        {
            PaymentLinkInformation result = await _service.GetOrder(orderId);
            return Ok();
        }
        [HttpGet("get-order-by/{userId}")]
        public async Task<IActionResult> GetPaymentbyUserId([FromRoute] Guid userId)
        {
            var result = await _service.GetPaymentbyUserId(userId);
            return Ok();
        }
        [HttpPut("cancel-order/{orderId}")]
        public async Task<IActionResult> CancelOrder([FromRoute] int orderId)
        {
            PaymentLinkInformation result = await _service.CancelOrder(orderId);
            return Ok();
        }
        [HttpGet("get-all")]
        public async Task<IEnumerable<Payment>> GetAll()
        {
            var result = await _service.GetAllAsync();
            return result;
        }

    }
}

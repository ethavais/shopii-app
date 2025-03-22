using APIService.Models;
using APIService.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Newtonsoft.Json;

namespace APIService.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ChatController : ControllerBase
    {
        private readonly IChatService _chatService;
        private readonly IHubContext<ChatHub> _hubContext; 
        public ChatController(IChatService chatService, IHubContext<ChatHub> hubContext)
        {
            _chatService = chatService;
            _hubContext = hubContext;
        }

        // User gửi tin nhắn
        [HttpPost("send")]
        public async Task<IActionResult> SendMessage([FromBody] SendMessageRequest request)
        {
            var (success, messageId) = await _chatService.SendMessage(request.UserId, request.Message, "user");
            if (success)
            {
                await _hubContext.Clients.All.SendAsync("ReceiveMessage", request.UserId, request.Message, "user");
                return Ok(new { MessageId = messageId });
            }
            return BadRequest("Failed to send message");
        }

        // Admin gửi tin nhắn trả lời user
        [HttpPost("admin/reply")]
        public async Task<IActionResult> AdminReply([FromBody] AdminReplyRequest request)
        {
            var (success, messageId) = await _chatService.SendMessage(
                request.UserId,
                request.Message,
                "admin"
            );

            if (success)
            {
                return Ok(new { MessageId = messageId });
            }
            return BadRequest(new { Error = "Failed to reply" });
        }

        // Lấy tin nhắn của một user
        [HttpGet("user/{userId}/messages")]
        public async Task<IActionResult> GetUserMessages(string userId)
        {
            var messages = await _chatService.GetMessagesForUser(userId);
            return Ok(messages);
        }

        // Lấy danh sách user đang chat
        [HttpGet("active-users")]
        public async Task<IActionResult> GetActiveUsers()
        {
            var users = await _chatService.GetActiveUsers();
            return Ok(users);
        }
        [HttpGet("latest")]
        public async Task<IActionResult> GetLatestMessage(string userId)
        {
            var firebaseClient = FirebaseConfigSetup.GetClient();
            var messages = await firebaseClient.GetAsync($"messages/{userId}");
            if (messages.Body == "null") return Ok(null);

            var messageDict = JsonConvert.DeserializeObject<Dictionary<string, ChatMessage>>(messages.Body);
            var latestMessage = messageDict.OrderByDescending(m => m.Value.Timestamp).FirstOrDefault();
            if (latestMessage.Value != null)
            {
                latestMessage.Value.Id = latestMessage.Key;
                return Ok(latestMessage.Value);
            }
            return Ok(null);
        }
    }

    public class SendMessageRequest
    {
        public string UserId { get; set; }
        public string Message { get; set; }
    }

    public class AdminReplyRequest
    {
        public string UserId { get; set; }
        public string Message { get; set; }
    }
}

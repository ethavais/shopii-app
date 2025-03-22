using APIService.Models;
using APIService.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace ChatShopiiAdmin.Pages
{
    public class IndexModel : PageModel
    {
        private readonly IChatService _chatService;
        private readonly IChatListener _chatListener;

        public IndexModel(IChatService chatService, IChatListener chatListener)
        {
            _chatService = chatService;
            _chatListener = chatListener;
        }

        public List<string> ActiveUsers { get; set; } = new List<string>();
        public List<ChatMessage> Messages { get; set; } = new List<ChatMessage>();
        public string SelectedUserId { get; set; }

        public async Task OnGetAsync(string userId = null)
        {
            // Lấy danh sách người dùng đang chat
            ActiveUsers = await _chatService.GetActiveUsers();

            if (!string.IsNullOrEmpty(userId))
            {
                SelectedUserId = userId;
                Messages = await _chatService.GetMessagesForUser(userId);

                // Lắng nghe tin nhắn từ user được chọn (Firebase)
                //_chatListener.ListenForMessages(userId, msg =>
                //{
                //    Messages.Add(msg);
                //    // Ghi log hoặc xử lý thêm nếu cần
                //    Console.WriteLine($"New message from {userId}: {msg.Text}");
                //});
            }
        }

    public async Task<IActionResult> OnPostSendMessageAsync(string userId, string message)
{
    Console.WriteLine($"OnPostSendMessageAsync called with userId: {userId}, message: {message}");
    if (!string.IsNullOrEmpty(userId) && !string.IsNullOrEmpty(message))
    {
        await _chatService.SendMessage(userId, message, "admin");
        Console.WriteLine("Message sent from OnPostSendMessageAsync");
        return new JsonResult(new { success = true }); // Trả về JSON thay vì render lại trang
    }
    return new JsonResult(new { success = false });
}

    }
}

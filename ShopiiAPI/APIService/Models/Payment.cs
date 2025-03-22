using System.ComponentModel.DataAnnotations;

namespace APIService.Models
{
    public class Payment
    {
        [Key]
        public long OrderCode { get; set; }
        public string Status { get; set; }
        public double Amount { get; set; }
        public Guid UserId { get; set; }
        public virtual ICollection<CardItem> CardItem { get; set; }
    }
}

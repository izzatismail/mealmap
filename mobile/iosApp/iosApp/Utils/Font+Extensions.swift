import SwiftUI

// Nunito typography scale matching MealMapTypography
extension Font {
    static let displayLarge = Font.custom("Nunito-ExtraBold", size: 24)
    static let headlineLarge = Font.custom("Nunito-ExtraBold", size: 22)
    static let titleLarge = Font.custom("Nunito-Bold", size: 18)
    static let bodyLarge = Font.custom("Nunito-SemiBold", size: 16)
    static let bodyMedium = Font.custom("Nunito-Regular", size: 14)
    static let labelLarge = Font.custom("Nunito-Bold", size: 13)
    static let bodySmall = Font.custom("Nunito-Regular", size: 12)
    static let labelSmall = Font.custom("Nunito-Bold", size: 11)
}

// Backward compatibility for views using .font(.system(size:weight:))
extension Font {
    static func nunito(size: CGFloat, weight: Font.Weight = .regular) -> Font {
        let name: String
        switch weight {
        case .black: name = "Nunito-ExtraBold"
        case .heavy: name = "Nunito-ExtraBold"
        case .bold: name = "Nunito-Bold"
        case .semibold: name = "Nunito-SemiBold"
        case .medium: name = "Nunito-SemiBold"
        default: name = "Nunito-Regular"
        }
        return Font.custom(name, size: size)
    }
}